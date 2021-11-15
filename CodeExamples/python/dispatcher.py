# -*- coding: utf-8 -*-
#Необходимо сделать нормальную работу Реле "Работа" и "Общая Авария"
import threading
import time
from promixlib.cnln01 import CN_LN_01
from promixlib.cnpr08 import CN_PR_08
import logging
from promixlib.workphase import WorkPhase
import string
from promixlib.hatchway import hatch
from promixlib.cntrltypes import ControlTypes
from promixlib.mytime import Timer
import serial
import serial.threaded
from promixlib.promixexceptions import AdapterConnectionException


class controller(threading.Thread):

    def __init__(self):
        threading.Thread.__init__(self)
        self.adapter = None
        self.phaze = WorkPhase.START
        self.work = True
        self.ARRAY_OF_CN_PR_08 = []
        self.ARRAY_OF_CN_PR_04 = []
        self.ARRAY_OF_CN_RD_01 = []
        self.ARRAY_OF_SM307    = []        
        self.ALARM_CONTRL_ADDR = 63
        self.RESERVED_ADDR = 62
        self.WORK_STATE_PIN =6
        self.ALARM_STATE_PIN = 7
        self.FIRE_ALARM_INPUT = 7
        self.MANUAL_BLOCK_INPUT = 6
        self.BLOCK = False
        self.MAX_CONTRL_COUNT = 64
        self.WORK_OK_STATE = True
        self.DOOR_CONTROLLERS = range(0,31)
        self.LIGHT_BUTTON_CONTROLLERS=range(31,62)
        self.TOTAL_DOORS_CONTRL_COUNTER = 0
        self.TOTAL_BTNS_CONTRL_COUNTER=0
        self.TOTAL_CONTROLS_COUNTER=0
        self.OPENED_DOOR = None
        self.TIMEOUTE = False
        self.ALARM_CONTROLLER = None
        self.TIMER_CANCELED = False
        self.serial_port_name = None
        self.ERROR_OPENED_DOORS = []
        self.ERROR_ANSWERED_CONTROLLERS = []
        self.WAITING_DOORS = []
        self.FAILURE = False
        self.MANUAL_BLOCK_VALUE = 0x00
        self.FIRE_ALARM_VALUE = 0x03
    

    def set_adpter(self, net_adapter=None):
        self.adapter = net_adapter

    def set_Event(self, event):
        self.finished = event

    def stop(self):
        self.work = False

    def timeouteHandler(self,value):
        if value=='Timer canceled':
            if self.TIMER_CANCELED == False:
                self.TIMER_CANCELED = True
            logging.getLogger('Timer').info('Таймер остановлен')            
        elif value == 'Timer timeoute':
            if self.TIMEOUTE == False:
                self.TIMEOUTE = True
            logging.getLogger('Timer').info('Вышло время ожидания')

    def run(self):
        print("Controller thread is runing\r\n")
        WaiteTimer = None
        self.log = logging.getLogger("Dispatcher")
        self.log.info('Запуск потока управления')
        firststart = True
        while self.work:
            if self.finished.is_set():
                break
            try:
                if    self.phaze is WorkPhase.START: # Запускаемся
                    self.TOTAL_DOORS_CONTRL_COUNTER = 0
                    self.TOTAL_CONTROLS_COUNTER = 0
                    self.TOTAL_BTNS_CONTRL_COUNTER = 0
                    if self.finished.is_set():
                        break                
                    if firststart:
                        self.log.info('Поиск доступных портов......')
                        #self.log.info("Available ports: ")
                    ports =serial.tools.list_ports.comports()                
                    for port in ports:
                        print(port.device)
                        if port.vid == 1240 and port.pid == 10:
                            self.serial_port_name = port.device
                            break
                    if self.serial_port_name != None:
                        self.log.info('Найден порт {}'.format(self.serial_port_name))
                        if self.adapter == None:
                            self.adapter = CN_LN_01()
                        self.adapter.set_Port(self.serial_port_name)                
                        self.phaze = WorkPhase.CONNECT
                    firststart = False
                    time.sleep(1)
                    continue
                elif  self.phaze is WorkPhase.CONNECT: # Соединяемся с адаптером и задаём ему настройки
                    if self.finished.is_set():
                        break   
                    self.adapter.enable_ManualMode()
                    if self.adapter.open() :
                        self.phaze = WorkPhase.SCAN_NET                    
                        continue
                    else: 
                        self.adapter.close()
                        i=0
                        while i < 10:
                            if self.finished.is_set():
                                self.work = False
                                break                        
                            time.sleep(1)
                            i+=1
                        continue
                elif  self.phaze is WorkPhase.SCAN_NET: # Сканируем сеть на наличие контроллеров
                    self.phasePass = 0
                    print('Scan net')                
                    self.log.info('Сканируем сеть')
                    found=0   
                    i=0
                    #if len(self.ARRAY_OF_CN_PR_08)==0:  # заполнение массива контроллеров      (self.adapter.CN_PR_08_COUNT > 0) and         
                    self.log.info('Поиск контроллеров CN.PR.08')
                    for i in range(self.MAX_CONTRL_COUNT):
                        if self.finished.is_set():
                            self.work = 0
                            self.phaze = WorkPhase.END
                            break
                        else:
                            item = CN_PR_08(i)
                            print('Ищем контроллер CN.PR.08 номер {}'.format(i))
                            readed = self.adapter.send_command(item.get_sensor_states(),4)
                            if  readed != None:
                                print('Обнаружен контроллер CN.PR.08 номер {}'.format(i))
                                self.log.info('Обнаружен контроллер CN.PR.08 номер {}'.format(i))
                                self.log.info('Производим настройку контроллера CN.PR.08 номер {}'.format(i))                                
                                cmd1=None
                                cmd2=None
                                if i == self.ALARM_CONTRL_ADDR:
                                    self.adapter.send_command(item.set_lock_time(self.WORK_STATE_PIN,240),0)
                                    self.adapter.send_command(item.open_lock(self.WORK_STATE_PIN),0)
                                    if self.FAILURE == True:
                                        self.adapter.send_command(item.open_lock(self.ALARM_STATE_PIN),0)
                                    self.adapter.send_command(item.set_lock_time(self.ALARM_STATE_PIN,0),0)
                                    if self.ALARM_CONTROLLER  == None:                                   
                                        self.TOTAL_CONTROLS_COUNTER += 1
                                        self.ALARM_CONTROLLER = item
                                elif i in self.DOOR_CONTROLLERS:
                                    cmd1 = item.set_AllLocks_time(0)
                                    cmd2 = item.close_AllLocks()
                                    if item not in self.ARRAY_OF_CN_PR_08:
                                        self.TOTAL_DOORS_CONTRL_COUNTER += 1
                                        self.ARRAY_OF_CN_PR_08.append(item)
                                elif i in self.LIGHT_BUTTON_CONTROLLERS:
                                    cmd1 = item.set_AllLocks_time(0)
                                    cmd2 = item.open_AllLocks()                                    
                                    if item not in self.ARRAY_OF_CN_PR_08:                                      
                                        self.TOTAL_BTNS_CONTRL_COUNTER += 1
                                        self.ARRAY_OF_CN_PR_08.append(item)
                               
                                if cmd1 != None:    
                                    for cmd in cmd1:
                                        if cmd != None:
                                            self.adapter.send_command(cmd,0)
                                if cmd2 != None:                                            
                                    for cmd in cmd2:
                                        if cmd != None:
                                            self.adapter.send_command(cmd,0)


                                found += 1                            
                            else:
                                #print('Check error for CN.PR.08  number {}'.format(i))
                                #logging.getLogger().info('Check error for CN.PR.08  number {}'.format(i))
                                pass

                    if(self.phasePass == 0):
                        self.phasePass += 1                      
                    self.log.info('Обнаруженно {} CN_PR_08'.format(found))
                    #logging.getLogger().info('Phase {}'.format(self.phaze))                    
                    print('Found {} CN_PR_08 controllers'.format(found))
                    #else:
                    #    if((self.phasePass & 1)==0):
                    #        self.phasePass += 1                          

                    if self.adapter.CN_PR_04_COUNT > 0:
                        if((self.phasePass & 2)==0):
                            self.phasePass += 2
                    else:
                        if((self.phasePass & 2)==0):
                            self.phasePass += 2
                        
                    if self.adapter.CN_RD_01_COUNT > 0:
                        if((self.phasePass & 4 )==0):
                            self.phasePass += 4
                    else:
                        if((self.phasePass & 4 )==0):
                            self.phasePass += 4
                    if self.phasePass == 7:
                        self.phaze = WorkPhase.WORK
                        continue
                    time.sleep(0.5)
                elif  self.phaze is WorkPhase.WORK: # Основной рабочий цикл
                    # вышло время ожидания открытия дверей
                    if self.TIMEOUTE:
                        WaiteTimer = None
                        self.TIMEOUTE = False
                        c = len(self.WAITING_DOORS)-1
                        while c>=0:
                            numb = self.WAITING_DOORS[c].CONTRL_NUMBER
                            pin = self.WAITING_DOORS[c].HATCH_NUMBER
                            if self.WAITING_DOORS[c].CONTRL_TYPE == ControlTypes.CN_PR_08:
                                self.adapter.send_command(CN_PR_08.close_ContlLock(numb,pin)) #Закрыть замок
                                self.adapter.send_command(CN_PR_08.open_ContlLock(numb+31,pin)) # Включить зелёный
                            del self.WAITING_DOORS[c]
                            c-=1                        
                        for contrl in self.ARRAY_OF_CN_PR_08:                        
                                commands = None
                                if contrl.CONTROLLER_NUMBER in self.DOOR_CONTROLLERS:
                                    commands = contrl.close_AllLocks()
                                elif contrl.CONTROLLER_NUMBER in self.LIGHT_BUTTON_CONTROLLERS:
                                    commands = contrl.open_AllLocks()
                                else:
                                    commands = None

                                if commands != None:
                                    for cmd in commands:
                                        self.adapter.send_command(cmd,0)
                                    time.sleep(0.3)

                    if self.TIMER_CANCELED:
                        WaiteTimer = None
                        self.TIMER_CANCELED = False
                        
                    if (len(self.ERROR_OPENED_DOORS) > 0) or (len(self.ERROR_ANSWERED_CONTROLLERS)>0) or (self.TOTAL_DOORS_CONTRL_COUNTER!=self.TOTAL_BTNS_CONTRL_COUNTER) or (self.TOTAL_CONTROLS_COUNTER<1): # Если есть ложно открыте двери или неответившие контроллеры то формируем аварию
                        if self.TOTAL_CONTROLS_COUNTER>0:
                            self.adapter.send_command(CN_PR_08.open_ContlLock(self.ALARM_CONTRL_ADDR,self.ALARM_STATE_PIN),0)
                        if self.FAILURE == False:
                            self.log.warning('Зафиксированно аварийное состояние')
                            self.log.warning('Количество контроллеров дверей {}'.format(self.TOTAL_DOORS_CONTRL_COUNTER))
                            self.log.warning('Количество контроллеров индикации и кнопок {}'.format(self.TOTAL_BTNS_CONTRL_COUNTER))
                            self.log.warning('Количество контроллеров отображения состояния {}'.format(self.TOTAL_CONTROLS_COUNTER))
                            self.log.warning('Количество ошибочно открытых дверей {}'.format(len(self.ERROR_OPENED_DOORS)))
                            for d in  self.ERROR_OPENED_DOORS:
                                self.log.warning(d)
                            self.log.warning('Количество неответивших контроллеров {}'.format(len(self.ERROR_ANSWERED_CONTROLLERS)))
                            for c in self.ERROR_ANSWERED_CONTROLLERS:
                                self.log.warning(c)
                            self.FAILURE = True
                            if self.TOTAL_DOORS_CONTRL_COUNTER == 0 or self.TOTAL_BTNS_CONTRL_COUNTER ==0 or self.TOTAL_CONTROLS_COUNTER ==0 or self.TOTAL_DOORS_CONTRL_COUNTER!=self.TOTAL_BTNS_CONTRL_COUNTER:
                                self.phaze = WorkPhase.SCAN_NET
                                continue                               
                    else:
                        self.adapter.send_command(CN_PR_08.close_ContlLock(self.ALARM_CONTRL_ADDR,self.ALARM_STATE_PIN),0)
                        if self.FAILURE == True:
                            self.FAILURE = False
                    
                    # Обработка управляющих сигналов от контроллера блокировок.
                    readed =self.adapter.send_command(self.ALARM_CONTROLLER.get_sensor_states(),4)                                                                      
                    if readed != None and len(readed) == 4:
                        if (self.ALARM_CONTROLLER in self.ERROR_ANSWERED_CONTROLLERS):
                            self.ERROR_ANSWERED_CONTROLLERS.remove(contrl) 
                        #включить реле работы
                        if(self.WORK_OK_STATE): 
                            #logging.getLogger().info('Hold work relay')
                            self.adapter.send_command(self.ALARM_CONTROLLER.open_lock(self.WORK_STATE_PIN),0) # Если всё хорошо то удерживаем реле Нормальной работы во включенном состоянии
                        else:
                            self.log.info('Что-то пошло не так')
                            self.adapter.send_command(self.ALARM_CONTROLLER.close_lock(self.WORK_STATE_PIN),0) # Если что-то не хорошо  то выключем его сигнализируюя об аварии
                        
                                                
                        instates = (readed[3]<<8)|(readed[2])
                        if(instates != self.ALARM_CONTROLLER.INPUTS_STATE):
                            alstate = self.ALARM_CONTROLLER.get_Value(self.MANUAL_BLOCK_INPUT,instates)
                            firestate = self.ALARM_CONTROLLER.get_Value(self.FIRE_ALARM_INPUT,instates)
                            message=''
                            if alstate == self.MANUAL_BLOCK_VALUE or firestate == self.FIRE_ALARM_VALUE:
                                if not self.BLOCK:
                                    self.BLOCK = True
                                    if self.OPENED_DOOR!= None:
                                        if self.OPENED_DOOR not in self.ERROR_OPENED_DOORS:
                                            self.ERROR_OPENED_DOORS.append(self.OPENED_DOOR)
                                            self.OPENED_DOOR = None
                                            if not self.FAILURE:
                                                self.FAILURE = True
                                    for cnt in self.ARRAY_OF_CN_PR_08:
                                        if (cnt.CONTROLLER_NUMBER in self.DOOR_CONTROLLERS) or  (cnt.CONTROLLER_NUMBER in self.LIGHT_BUTTON_CONTROLLERS):
                                            commands = cnt.close_AllLocks()
                                            for cmd in commands:
                                                if cnt.CONTROLLER_NUMBER<31:
                                                    h = hatch(ControlTypes.CN_PR_08,cnt.CONTROLLER_NUMBER,cmd[-1])
                                                else:
                                                     h = hatch(ControlTypes.CN_PR_08,cnt.CONTROLLER_NUMBER-31,cmd[-1])
                                                if h not in self.ERROR_OPENED_DOORS:
                                                    self.adapter.send_command(cmd,0)
                                    message=''
                                    if alstate == self.MANUAL_BLOCK_VALUE and firestate !=self.FIRE_ALARM_VALUE:
                                        message = 'Ручная блокировка'
                                        self.log.info(message)
                                    elif alstate != self.MANUAL_BLOCK_VALUE and firestate == self.FIRE_ALARM_VALUE:
                                        message = 'ПОЖАРНАЯ ТРЕВОГА'
                                        self.log.info(message)
                                    elif alstate == self.MANUAL_BLOCK_VALUE and firestate == self.FIRE_ALARM_VALUE:
                                        message = 'Ручная блокировка и ПОЖАРНАЯ ТРЕВОГА'
                                        self.log.info(message)                                               
                            else:
                                if self.BLOCK:
                                    self.BLOCK = False
                                    message = 'Блокировки сняты!!!!'
                                    self.log.info(message)
                                    for c in self.ARRAY_OF_CN_PR_08:
                                        if c.CONTROLLER_NUMBER in self.LIGHT_BUTTON_CONTROLLERS:
                                            c.INPUTS_STATE = 0xFFFF
                                        elif c.CONTROLLER_NUMBER in self.DOOR_CONTROLLERS:
                                            c.INPUTS_STATE = 0x0000
                                    # Тут нужно вернуть всё в зад
                                    if len(self.ERROR_OPENED_DOORS)==0 and self.OPENED_DOOR == None:
                                        cmds = []
                                        for cnt in self.ARRAY_OF_CN_PR_08:
                                            if cnt.CONTROLLER_NUMBER in self.LIGHT_BUTTON_CONTROLLERS:
                                                cmds = cnt.open_AllLocks()
                                            elif cnt.CONTROLLER_NUMBER in self.DOOR_CONTROLLERS:
                                                cmds = cnt.close_AllLocks()
                                            for cmd in cmds:
                                                self.adapter.send_command(cmd,0)
                                    else:
                                        if len(self.ERROR_OPENED_DOORS)>0:
                                            for eod in self.ERROR_OPENED_DOORS:
                                                cnumber = eod.CONTRL_NUMBER
                                                lnumber = eod.HATCH_NUMBER
                                                self.adapter.send_command(CN_PR_08.open_door(cnumber,lnumber),0)
                                                self.adapter.send_command(CN_PR_08.open_door(cnumber+31,lnumber),0)
                                        if self.OPENED_DOOR != None:
                                            self.adapter.send_command(CN_PR_08.open_door(self.OPENED_DOOR.CONTRL_NUMBER,self.OPENED_DOOR.HATCH_NUMBER),0)
                                            self.adapter.send_command(CN_PR_08.open_door((self.OPENED_DOOR.CONTRL_NUMBER)+31,self.OPENED_DOOR.HATCH_NUMBER),0)     

                            self.ALARM_CONTROLLER.INPUTS_STATE = instates
                            message = ''
                    else:
                        if(contrl not in self.ERROR_ANSWERED_CONTROLLERS):
                            self.ERROR_ANSWERED_CONTROLLERS.append(contrl)
                            if not self.FAILURE:
                                self.FAILURE = True

                    for contrl in self.ARRAY_OF_CN_PR_08:
                        readed =self.adapter.send_command(contrl.get_sensor_states(),4)
                        # читаем состояния входов  контроллера                            
                        if readed != None:
                            #if '{}'.format(contrl.CONTROLLER_NUMBER) in self.ERROR_ANSWERED_CONTROLLERS:
                            #    self.ERROR_ANSWERED_CONTROLLERS.remove('{}'.format(contrl.CONTROLLER_NUMBER))
                            if contrl in self.ERROR_ANSWERED_CONTROLLERS:
                                self.ERROR_ANSWERED_CONTROLLERS.remove(contrl)
                            if len(readed) == 4:                        
                                instates = (readed[3]<<8)|(readed[2])
                                if ( contrl.INPUTS_STATE != instates):
                                    pinchanged=contrl.get_InputChangegNumbers(instates) #Получаем номера пинов, поменявших состояние.

                                    # Тут должна быть реализация основного алгоритма                                                                     
                                    if contrl.CONTROLLER_NUMBER in self.LIGHT_BUTTON_CONTROLLERS :
                                        if not self.BLOCK:
                                            for pin in pinchanged:
                                                if contrl.get_Value(pin,instates) == 0: # Нажата кнопка открытия двери
                                                    self.log.info('Нажата кнопка открытия двери {}.{}'.format(contrl.CONTROLLER_NUMBER,pin))
                                                    if self.OPENED_DOOR == None:
                                                        if WaiteTimer == None:
                                                            WaiteTimer = Timer(self.timeouteHandler,30)
                                                            WaiteTimer.start()
                                                        #door = '{}.{}'.format(((contrl.CONTROLLER_NUMBER) - 31),pin)
                                                        door = hatch(ControlTypes.CN_PR_08,((contrl.CONTROLLER_NUMBER) - 31),pin)
                                                        if door not in self.WAITING_DOORS:
                                                            self.WAITING_DOORS.append(door)                                                
                                                        self.adapter.send_command(CN_PR_08.open_ContlLock(((contrl.CONTROLLER_NUMBER) - 31),pin),0) # открываем соответсвующий замок
                                                        self.adapter.send_command(contrl.open_lock(pin),0) #включаем зелёную лампу
                                        # Сохранить новое значение в контроллере 
                                        contrl.INPUTS_STATE = instates                                                
                                        continue                                                
                                    elif contrl.CONTROLLER_NUMBER in self.DOOR_CONTROLLERS:
                                        #if not self.BLOCK:
                                        for pin in pinchanged:
                                            st = contrl.get_Value(pin,instates)
                                            #self.log.info('Изменилось состояние входа {}.{} на {}'.format(contrl.CONTROLLER_NUMBER,pin,st))
                                            
                                            if st == 0x03: # дверь открыта
                                                if WaiteTimer != None:
                                                    WaiteTimer.cancel()
                                                #opdoor = '{}.{}'.format(contrl.CONTROLLER_NUMBER,pin)
                                                opdoor = hatch(ControlTypes.CN_PR_08,contrl.CONTROLLER_NUMBER,pin)
                                                self.log.info('Открыта дверь {}.{}'.format(contrl.CONTROLLER_NUMBER,pin))
                                                if opdoor not in self.WAITING_DOORS:
                                                    if opdoor not in self.ERROR_OPENED_DOORS and self.OPENED_DOOR != opdoor:
                                                        if len(self.ERROR_OPENED_DOORS)==0:
                                                            for bcontrol in self.ARRAY_OF_CN_PR_08:
                                                                
                                                                cmd =[]
                                                                
                                                                if bcontrol.CONTROLLER_NUMBER in self.DOOR_CONTROLLERS:
                                                                    cmd = bcontrol.close_AllLocks()
                                                                if bcontrol.CONTROLLER_NUMBER in self.LIGHT_BUTTON_CONTROLLERS:
                                                                    cmd = bcontrol.close_AllLocks()

                                                                for c in cmd:
                                                                    if self.OPENED_DOOR != None:
                                                                        if c[-1] != self.OPENED_DOOR.HATCH_NUMBER and c[1] != self.OPENED_DOOR.CONTRL_NUMBER:
                                                                            self.adapter.send_command(c,0)
                                                                    else:
                                                                        self.adapter.send_command(c,0)
                                                        self.ERROR_OPENED_DOORS.append(opdoor)
                                                        self.adapter.send_command(contrl.open_lock(pin),0)
                                                        self.adapter.send_command(CN_PR_08.open_ContlLock(contrl.CONTROLLER_NUMBER+31,pin),0)
                                                                                                
                                                else:
                                                    if self.OPENED_DOOR == None:
                                                        self.OPENED_DOOR = opdoor
                                                        
                                                        if len(self.WAITING_DOORS)>0:
                                                            c = len(self.WAITING_DOORS)-1
                                                            while c>=0:
                                                                del self.WAITING_DOORS[c]
                                                                c-=1 
                                                        for bcontrol in self.ARRAY_OF_CN_PR_08:
                                                            remote = bcontrol.CONTROLLER_NUMBER
                                                            if remote in self.DOOR_CONTROLLERS:
                                                                for i in range(8):
                                                                    cond1 = ((bcontrol.CONTROLLER_NUMBER) == contrl.CONTROLLER_NUMBER)
                                                                    cond2 = (i == pin)
                                                                    if ( cond1) and (cond2):
                                                                        self.adapter.send_command(bcontrol.open_lock(i),0) # открываем открытый замок                                                  
                                                                    else:
                                                                        self.adapter.send_command(bcontrol.close_lock(i),0) # Блокируем замки на всех дверях кроме открыторй
                                                            elif remote in self.LIGHT_BUTTON_CONTROLLERS:
                                                                for i in range(8):
                                                                    if ((remote-31) == contrl.CONTROLLER_NUMBER) and (i == pin):
                                                                        self.adapter.send_command(bcontrol.open_lock(i),0) # Включем зелёную лампу на открытой двери
                                                                    else:
                                                                        self.adapter.send_command(bcontrol.close_lock(i),0) # Включем красные лампы на всех блокируемых дверях
                                                                    
                                                    else:

                                                        self.log.info('Но уже открыта дверь {}.{}'.format(self.OPENED_DOOR.CONTRL_NUMBER,self.OPENED_DOOR.HATCH_NUMBER))

                                            elif st == 0: # дверь закрыта
                                                #closed_door = '{}.{}'.format(contrl.CONTROLLER_NUMBER,pin)
                                                closed_door = hatch(ControlTypes.CN_PR_08,contrl.CONTROLLER_NUMBER,pin)
                                                if closed_door in  self.ERROR_OPENED_DOORS:
                                                    self.log.info('Закрытая дверь ранне была несанкционированно открыта, удаляем из списка.')
                                                    self.ERROR_OPENED_DOORS.remove(closed_door)
                                                    if self.FAILURE:
                                                        self.log.info('Выключаем индикацию и замок закрытой двери')
                                                        self.adapter.send_command(contrl.close_lock(pin),0)
                                                        self.adapter.send_command(CN_PR_08.close_ContlLock(contrl.CONTROLLER_NUMBER+31,pin),0)
                                                else:
                                                    for item in self.ERROR_OPENED_DOORS:
                                                        self.log.info('ITEM of ERROR_OPENED_DOORS: {}'.format(item))
                                                    
                                                                                            
                                                if self.OPENED_DOOR != None:                                                    
                                                    if self.OPENED_DOOR.get_ContrlNumber() == contrl.CONTROLLER_NUMBER:
                                                        self.log.info('Закрылась дверь {}.{}'.format(contrl.CONTROLLER_NUMBER,pin))
                                                        if self.OPENED_DOOR.get_HatchNumber() == pin:
                                                            self.adapter.send_command(contrl.close_lock(pin),0)
                                                            if not self.FAILURE:
                                                                for item in self.ARRAY_OF_CN_PR_08:
                                                                    if item.CONTROLLER_NUMBER in self.LIGHT_BUTTON_CONTROLLERS:
                                                                        commands = item.open_AllLocks()
                                                                        for cmd in commands:                                                                    
                                                                            self.adapter.send_command(cmd,0) # включаем зелёную лампу
                                                            else:
                                                                if len(self.ERROR_OPENED_DOORS)==0:
                                                                        for item in self.ARRAY_OF_CN_PR_08:
                                                                            if item.CONTROLLER_NUMBER in self.LIGHT_BUTTON_CONTROLLERS:
                                                                                commands = item.open_AllLocks()
                                                                                for cmd in commands:                                                                    
                                                                                    self.adapter.send_command(cmd,0) # включаем зелёную лампу
                                                            self.OPENED_DOOR = None
                                                    
                                                else:
                                                    if len(self.ERROR_OPENED_DOORS)==0:
                                                        if not self.BLOCK:
                                                            for item in self.ARRAY_OF_CN_PR_08:
                                                                if item.CONTROLLER_NUMBER in self.LIGHT_BUTTON_CONTROLLERS:
                                                                    commands = item.open_AllLocks()
                                                                    for cmd in commands:                                                                    
                                                                        self.adapter.send_command(cmd,0) # включаем зелёную лампу
                                                    self.log.info('Закрыта дверь {}.{} но открытий небыло зафиксированно!'.format(contrl.CONTROLLER_NUMBER,pin))
                                        contrl.INPUTS_STATE = instates # Сохранить новое значение в контроллере
                                        continue                        
                            else:
                                self.WORK_OK_STATE = False
                        else:
                            self.log.info('Неудалось получить ответ от контроллера {}'.format(contrl.CONTROLLER_NUMBER))
                            if contrl not in self.ERROR_ANSWERED_CONTROLLERS:
                                self.ERROR_ANSWERED_CONTROLLERS.append(contrl)
                    # тут должна быть работа с другими контроллерами.
                    continue
                elif  self.phaze is WorkPhase.END:
                    self.work = False
            except AdapterConnectionException:
                firststart = True
                self.phaze = WorkPhase.START
                self.adapter.close()
                self.serial_port_name = None
                count=len(self.ARRAY_OF_CN_PR_08)-1
                while count>=0:
                    del self.ARRAY_OF_CN_PR_08[count]
                    count-=1
                time.sleep(5)
        if self.adapter != None:
            if self.adapter.CONNECTED:
                cmd1 = self.ALARM_CONTROLLER.close_AllLocks()
                for c in cmd1:
                    self.adapter.send_command(c,0)
                for item in self.ARRAY_OF_CN_PR_08:
                    commands = item.close_AllLocks()
                    for cmd in commands:
                        self.adapter.send_command(cmd,0)
            self.adapter.close()    
        print("Завершение потока управления")