
package oko3c.al;

import com.oko.lang.utils.StringPraser;
import com.oko.lang.utils.Util;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Timer;
import java.util.Vector;
import javax.microedition.io.Connector;

import oko3c.al.communicate.Call;
import oko3c.al.communicate.CommunicationManager;
import oko3c.al.communicate.ICommunication;
import oko3c.al.communicate.Point;
import oko3c.al.communicate.ak.AkMessage;
import oko3c.al.communicate.ak.AkMessage2;
import oko3c.al.communicate.ak.AkMessageFilter;
import oko3c.al.communicate.ak.OKO2Mes;
import oko3c.al.communicate.gsm.Parser;
import oko3c.al.communicate.gsm.gsmMon;
import oko3c.al.communicate.http.Downloader;
import oko3c.al.communicate.indication.Iindicator;
import oko3c.al.communicate.indication.Indicator;
import oko3c.al.communicate.rs232.AK8Handler;
import oko3c.al.communicate.rs232.IComEvent;
import oko3c.al.communicate.sms.SMSMessage;
import org.json.me.JSONArray;
import org.json.me.JSONException;
import org.json.me.JSONObject;

//#ifdef EHS5Autostart
//# import javax.microedition.media.Manager;
//# import javax.microedition.media.MediaException;
//# import javax.microedition.io.file.FileConnection;
//# import com.cinterion.io.ATCommand;
//# import com.cinterion.io.ATCommandFailedException;
//# import com.cinterion.io.ATCommandListener;
//# import com.cinterion.io.ATCommandResponseListener;
//# import oko3c.al.ATExtends;
//# import com.cinterion.io.OutPort;
//# import oko3c.al.communicate.calling.ATDAnswerListener;
//# import oko3c.al.communicate.rs232.PacketHandler;
//# import oko3c.al.communicate.http.IDownloader;
//# import oko3c.hl.TerminalState;
//# import oko3c.al.ATCmdEndAction;
//#define EHS
//#elifdef EHS5Debug
//# import javax.microedition.media.Manager;
//# import javax.microedition.media.MediaException;
//# import javax.microedition.io.file.FileConnection;
//# import com.cinterion.io.ATCommand;
//# import com.cinterion.io.ATCommandFailedException;
//# import com.cinterion.io.ATCommandListener;
//# import com.cinterion.io.ATCommandResponseListener;
//# import oko3c.al.ATExtends;
//# import com.cinterion.io.OutPort;
//# import oko3c.al.communicate.rs232.PacketHandler;
//# import oko3c.al.communicate.calling.ATDAnswerListener;
//# import oko3c.al.communicate.http.IDownloader;
//# import oko3c.hl.TerminalState;
//# import oko3c.al.ATCmdEndAction;
//#define EHS
//#elifdef EHS5AutostartOldInd
//# import javax.microedition.media.Manager;
//# import javax.microedition.media.MediaException;
//# import javax.microedition.io.file.FileConnection;
//# import com.cinterion.io.ATCommand;
//# import com.cinterion.io.ATCommandFailedException;
//# import com.cinterion.io.ATCommandListener;
//# import com.cinterion.io.ATCommandResponseListener;
//# import oko3c.al.ATExtends;
//# import com.cinterion.io.OutPort;
//# import oko3c.al.communicate.rs232.PacketHandler;
//# import oko3c.al.communicate.calling.ATDAnswerListener;
//# import oko3c.al.communicate.http.IDownloader;
//# import oko3c.hl.TerminalState;
//# import oko3c.al.ATCmdEndAction;
//#define EHS
//#elifdef EHS5DebugOldInd
//# import javax.microedition.media.Manager;
//# import javax.microedition.media.MediaException;
//# import javax.microedition.io.file.FileConnection;
//# import com.cinterion.io.ATCommand;
//# import com.cinterion.io.ATCommandFailedException;
//# import com.cinterion.io.ATCommandListener;
//# import com.cinterion.io.ATCommandResponseListener;
//# import oko3c.al.ATExtends;
//# import com.cinterion.io.OutPort;
//# import oko3c.al.communicate.rs232.PacketHandler;
//# import oko3c.al.communicate.calling.ATDAnswerListener;
//# import oko3c.al.communicate.http.IDownloader;
//# import oko3c.hl.TerminalState;
//# import oko3c.al.ATCmdEndAction;
//#define EHS
//#elifdef BGS5Autostart
//# import javax.microedition.io.file.*;
//# import com.cinterion.io.ATCommand;
//# import com.cinterion.io.ATCommandFailedException;
//# import com.cinterion.io.ATCommandListener;
//# import com.cinterion.io.ATCommandResponseListener;
//# import oko3c.al.ATExtends;
//# import com.cinterion.io.OutPort;
//# import oko3c.al.communicate.rs232.PacketHandler;
//# import oko3c.al.communicate.calling.ATDAnswerListener;
//# import oko3c.al.communicate.http.IDownloader;
//# import oko3c.hl.TerminalState;
//# import oko3c.al.ATCmdEndAction;
//#define BGS
//#elifdef BGS5Debug
//# import javax.microedition.io.file.*;
//# import com.cinterion.io.ATCommand;
//# import com.cinterion.io.ATCommandFailedException;
//# import com.cinterion.io.ATCommandListener;
//# import com.cinterion.io.ATCommandResponseListener;
//# import oko3c.al.ATExtends;
//# import com.cinterion.io.OutPort;
//# import oko3c.al.communicate.calling.ATDAnswerListener;
//# import oko3c.al.communicate.rs232.PacketHandler;
//# import oko3c.al.communicate.http.IDownloader;
//# import oko3c.hl.TerminalState;
//# import oko3c.al.ATCmdEndAction;
//#define BGS
//#elifdef BGS5AutostartOldInd
//# import javax.microedition.io.file.*;
//# import com.cinterion.io.ATCommand;
//# import com.cinterion.io.ATCommandFailedException;
//# import com.cinterion.io.ATCommandListener;
//# import com.cinterion.io.ATCommandResponseListener;
//# import oko3c.al.ATExtends;
//# import com.cinterion.io.OutPort;
//# import oko3c.al.communicate.rs232.PacketHandler;
//# import oko3c.al.communicate.calling.ATDAnswerListener;
//# import oko3c.al.communicate.http.IDownloader;
//# import oko3c.hl.TerminalState;
//# import oko3c.al.ATCmdEndAction;
//#define BGS
//#elifdef BGS5DebugOldInd
//# import javax.microedition.io.file.*;
//# import com.cinterion.io.ATCommand;
//# import com.cinterion.io.ATCommandFailedException;
//# import com.cinterion.io.ATCommandListener;
//# import com.cinterion.io.ATCommandResponseListener;
//# import oko3c.al.ATExtends;
//# import com.cinterion.io.OutPort;
//# import oko3c.al.communicate.rs232.PacketHandler;
//# import oko3c.al.communicate.calling.ATDAnswerListener;
//# import oko3c.al.communicate.http.IDownloader;
//# import oko3c.hl.TerminalState;
//# import oko3c.al.ATCmdEndAction;
//#define BGS
//#elifdef TC65Gpio
//# import com.siemens.icm.io.ATCommand;
//# import com.siemens.icm.io.ATCommandFailedException;
//# import com.siemens.icm.io.ATCommandListener;
//# import com.siemens.icm.io.ATCommandResponseListener;
//# import oko3c.al.ATExtends;
//# import com.siemens.icm.io.OutPort;
//# import com.siemens.icm.io.file.FileConnection;
//# import com.siemens.icm.misc.Watchdog;
//# import javax.microedition.media.Manager;
//# import javax.microedition.media.MediaException;
//# import oko3c.al.communicate.rs232.PacketHandler;
//# import oko3c.al.communicate.calling.ATDAnswerListener;
//# import oko3c.al.communicate.http.IDownloader;
//# import oko3c.hl.TerminalState;
//# import oko3c.al.ATCmdEndAction;
//#elifdef TC65GpioDebug
//# import com.siemens.icm.io.ATCommand;
//# import com.siemens.icm.io.ATCommandFailedException;
//# import com.siemens.icm.io.ATCommandListener;
//# import com.siemens.icm.io.ATCommandResponseListener;
//# import oko3c.al.ATExtends;
//# import com.siemens.icm.io.OutPort;
//# import com.siemens.icm.io.file.FileConnection;
//# import javax.microedition.media.Manager;
//# import javax.microedition.media.MediaException;
//# import oko3c.al.communicate.rs232.PacketHandler;
//# import oko3c.al.communicate.calling.ATDAnswerListener;
//# import oko3c.al.communicate.http.IDownloader;
//# import oko3c.hl.TerminalState;
//# import oko3c.al.ATCmdEndAction;
//#elifdef TC65Spi
//# import com.siemens.icm.io.ATCommand;
//# import oko3c.al.ATExtends;
//# import com.siemens.icm.io.ATCommandFailedException;
//# import com.siemens.icm.io.ATCommandListener;
//# import com.siemens.icm.io.ATCommandResponseListener;
//# import com.siemens.icm.io.OutPort;
//# import com.siemens.icm.io.file.FileConnection;
//# import javax.microedition.media.Manager;
//# import javax.microedition.media.MediaException;
//# import oko3c.al.communicate.rs232.PacketHandler;
//# import oko3c.al.communicate.calling.ATDAnswerListener;
//# import oko3c.al.communicate.http.IDownloader;
//# import oko3c.hl.TerminalState;
//# import oko3c.al.ATCmdEndAction;
//#elifdef TC65SpiConcept
//# import com.siemens.icm.io.ATCommand;
//# import com.siemens.icm.io.ATCommandFailedException;
//# import com.siemens.icm.io.ATCommandListener;
//# import com.siemens.icm.io.ATCommandResponseListener;
//# import com.siemens.icm.io.OutPort;
//# import com.siemens.icm.io.file.FileConnection;
//# import javax.microedition.media.Manager;
//# import javax.microedition.media.MediaException;
//# import oko3c.al.communicate.rs232.PacketHandler;
//# import oko3c.al.communicate.calling.ATDAnswerListener;
//# import oko3c.al.communicate.http.IDownloader;
//# import oko3c.hl.TerminalState;
//# import oko3c.al.ATExtends;
//# import oko3c.al.ATCmdEndAction;
//#elifdef TC65SpiConceptDebug
//# import com.siemens.icm.io.ATCommand;
//# import com.siemens.icm.io.ATCommandFailedException;
//# import com.siemens.icm.io.ATCommandListener;
//# import com.siemens.icm.io.ATCommandResponseListener;
//# import com.siemens.icm.io.OutPort;
//# import com.siemens.icm.io.file.FileConnection;
//# import javax.microedition.media.Manager;
//# import javax.microedition.media.MediaException;
//# import oko3c.al.communicate.rs232.PacketHandler;
//# import oko3c.al.communicate.calling.ATDAnswerListener;
//# import oko3c.al.communicate.http.IDownloader;
//# import oko3c.hl.TerminalState;
//# import oko3c.al.ATExtends;
//# import oko3c.al.ATCmdEndAction;
//#elifdef TC65SpiDebug
//# import com.siemens.icm.io.ATCommand;
//# import com.siemens.icm.io.ATCommandFailedException;
//# import com.siemens.icm.io.ATCommandListener;
//# import com.siemens.icm.io.ATCommandResponseListener;
//# import com.siemens.icm.io.OutPort;
//# import com.siemens.icm.io.file.FileConnection;
//# import javax.microedition.media.Manager;
//# import javax.microedition.media.MediaException;
//# import oko3c.al.communicate.rs232.PacketHandler;
//# import oko3c.al.communicate.calling.ATDAnswerListener;
//# import oko3c.al.communicate.http.IDownloader;
//# import oko3c.hl.TerminalState;
//# import oko3c.al.ATExtends;
//# import oko3c.al.ATCmdEndAction;
//#endif

/**
 *
 * @author User
 */
public class Terminal implements Runnable, ATCommandListener, ATCommandResponseListener, Iindicator, IComEvent, ICommunication, IDownloader {

    //static and constatnts
    private static final byte work_led = (byte) 0;
    private static final byte call_led = (byte) 1;
    private static final char _TC65_PLATFORM = 1;
    private static final char _BGS5_PLATFORM = 3;
    private static final char _EHS5_PLATFORM = 4;
    private static final char _PLATFORM_UNDEFINED = 0xFFFF;
    private static final char _NONE_STATE = 0;
    private static final char _START_STATE = 1;
    private static final char _INIT_AT_STATE = 2;
    private static final char _INIT_AT_STATE_PROCESS = 3;
    private static final char _INIT_STATE = 4;
    private static final char _INIT_STATE_PROCESS = 5;
    private static final char _WORK_STATE = 6;
    private static final char _RESTART_GSM_STATE = 7;
    private static final char _UPDATE_FIRMWARE_STATE = 8;
    private static final char _UPDATE_CONFIG_STATE = 9;
    private static final char _AIRPLAINE_STATE = 10;
    private static final char _RESTART_BY_CALL_STATE = 253;
    private static final char _RESTART_STATE = 254;
    private static final char _STATE_UNDEFINED = 255;
    private volatile static  TerminalState _TERM_STATE;
    private static Terminal instance;
    static {
        try {
            instance = new Terminal();
        } catch (ATCommandFailedException ex) {
            ex.printStackTrace();
        }
    }
    /**
     *
     * @return
     */
    public static Terminal getInstance() {
//        if (instance == null) {
//            try {
//                instance = new Terminal();
//            } catch (Exception e) {
//            }
//
//        }
return instance;
    }
    private Thread _WORK;
    private final AK8Handler _COM;
    
//    private Coordinator _COORDINATOR;
    private boolean _PBREADY;
    private boolean _SYSSTART;
    private boolean _AIRPLANE_MODE;

    //volatile
    private volatile long _LOOP_IT;
    private volatile boolean _FLAG;
    private volatile Indicator _INDICATION;
    private volatile char _MODE;
    private volatile char _OLD_MODE;
    private volatile byte _CALL_STATE;
    private volatile char _SERVICE;
    private volatile char _REGISTRATION;
    private volatile Vector _WHITE_LIST;
    private volatile Vector _RESTART_BY_CALL_LIST;
    private volatile Vector _CALL_TO_LIST;
    private volatile Vector _SOS_LIST;
    private volatile Vector _REMOTE_CONTROL_LIST;
    private volatile int _ObjectID;
    private volatile char _NET_MODE;
    private volatile OutPort _AUDIO;
    private volatile long _LAST_LIVE_EVENT;
    private volatile int _LIVE_PERIOD;

    //final
//    private final ATCommand _ATC;
    private final ATExtends _ATC;
    private final Object _MUTEX;
    private final Vector _MESSAGES;
    private final Vector _QUEUE;
    private final oko3c.al.communicate.CommunicationManager _FAR_WAYS;
    private final String _WorkDir;
    private Timer _TIMER_LIVE;
    private Timer _TIMER_GSMRST;
    private Timer _CALL_TIMER;
    private Timer _START_TIMER;
    private int _MESSAGE_NUBER;
    private Call _LAST_CALL;
    private JSONObject _CURRENT_CONFIG;
    private String _VERSION;
    private String _CID;
    private String _SPY_NUMBER;
    //private final gsmMon _MONITOR;
    private int _CALL_TIME;
    private volatile boolean _RESTART_GSM;
    private volatile int _SIGNAL_LEVEL;
    private char _RACCT;
    private PacketHandler _PacketHandler;
//    private final ATDAnswerListener _CALL_HANDLER;

    private char _PLATFORM;
    
    private char _STATE;
    private char _PREV_STATE;
    private String _CURRENT_OPERATOR_NAME;
    private int _CURRENT_OPERATOR_ID;
    private int _AIRPLANE_MODE_COUNTER;
    private boolean _JAVA_NET;
    private String _JAVA_NET_NAME;
    private StringBuffer workFlowbuf;
    private boolean _FILE_UPDATED;
    private long _FILE_UPDATE_START_TIME;
    private Downloader _DOWNLOADER;
    private boolean _AIRPLANE_CHANGED;
    private boolean  _FIRST_START;
    private boolean send_once;
    
    private Terminal() throws ATCommandFailedException {
        _TERM_STATE = new  TerminalState();
        _ATC = new ATExtends(false);
        _QUEUE = new Vector(0);
        _MESSAGES = new Vector(0);
        _MODE = (byte) 0;
        _CALL_STATE = (byte) 0;
        _SERVICE = (char) 1;
        _REGISTRATION = (char) 0;
        _WHITE_LIST = new Vector(0);
        _SOS_LIST = new Vector(0);
        _RESTART_BY_CALL_LIST = new Vector(0);
        _REMOTE_CONTROL_LIST = new Vector(0);
        _CALL_TO_LIST = new Vector(3);
        _CALL_TO_LIST.addElement("");
        _CALL_TO_LIST.addElement("");
        _CALL_TO_LIST.addElement("");
        _MUTEX = new Object();
        _PacketHandler = new PacketHandler(this);
        _COM = new AK8Handler(_PacketHandler);
        _FAR_WAYS = new CommunicationManager();
        _FAR_WAYS.set_ATExtends(_ATC);
        _WorkDir = "a:/storage";
//        _TIMER= new Timer();
        _MESSAGE_NUBER = 1;
        //_MONITOR = new gsmMon(_ATC);
        _CID = "";
        _PBREADY = false;
        _SYSSTART = false;
        _LIVE_PERIOD = 0;
        _LAST_LIVE_EVENT = System.currentTimeMillis();
//        _CALL_HANDLER = new ATDAnswerListener();
        _AIRPLANE_MODE = false;
        _PREV_STATE = _STATE_UNDEFINED;
        _STATE = _START_STATE;
        _JAVA_NET = false;
        _JAVA_NET_NAME = "";
        workFlowbuf = new StringBuffer();
        _CURRENT_OPERATOR_NAME="";
        _DOWNLOADER = null;
        _AIRPLANE_CHANGED = false;
        _FIRST_START = false;
        send_once=false;

    }

    
    /**
     *
     */
    public void initComponents() {
        //_ATC.addListener(this);
        if (_CALL_TO_LIST.isEmpty()) {
            _CALL_TO_LIST.addElement("");
            _CALL_TO_LIST.addElement("");
            _CALL_TO_LIST.addElement("");
        }        
        System.out.println("Terminal.initComponents(): _ATC.addListener((ATCommandListener) this)="+_ATC.addListener((ATCommandListener) this));        
        _ATC.begin();
    }

    /**
     *
     * @param version
     */
    public void set_Version(String version) {
        this._VERSION = version;
    }

    private void workflow() {
//        System.out.println("Terminal: workflow");
        updateTimeCounter();
        Object msg = null;
        long conditions = 0;
        long millis = 1000;
        int messagescount = 0;
        if (workFlowbuf.length() > 0) {
            workFlowbuf.delete(0, workFlowbuf.length());
        }
        conditions = (_LIVE_PERIOD * 3600 * 1000) + 180000;
        if (System.currentTimeMillis() - _LAST_LIVE_EVENT >= conditions) {
            System.out.print("_LOOP_IT: " + _LOOP_IT + "\r\n_LAST_LIVE_EVENT: " + _LAST_LIVE_EVENT + "\r\n_LOOP_IT - _LAST_LIVE_EVENT: " + (_LOOP_IT - _LAST_LIVE_EVENT) + "\r\n_LIVE_PERIOD: " + _LIVE_PERIOD + "\r\n(_LIVE_PERIOD * 3600 * 1000) + 180000):" + ((_LIVE_PERIOD * 3600 * 1000) + 180000));
            //проверяем и запускаем таймер контрольных;
            try {
                if (_MODE != CommunicationManager.EXTERNAL_BLOCKED_MODE) {
                    Task ts = new Task(this, new LiveEvent(), "Live");
                    if (_TIMER_LIVE != null) {
                        _TIMER_LIVE.cancel();
                        _TIMER_LIVE = null;
                    }
                    if (_TIMER_LIVE == null) {
                        _TIMER_LIVE = new Timer();
                    }

                    _TIMER_LIVE.schedule(ts, 0, _LIVE_PERIOD * 3600 * 1000);
                }


            } catch (Exception ex) {
                System.out.println("[Terminal] AcceptConfig-LivePeriod");
            } catch (Error err) {
            }
        }
        synchronized (_QUEUE) {

            try {
               // _QUEUE.wait(millis);//Приостановка потока на 1 сек.
                if (!_QUEUE.isEmpty()) {
                    try {
                        workFlowbuf.append((String) _QUEUE.firstElement());
                    } catch (NullPointerException ex) {
                    } catch (ClassCastException ex) {
                    }
                    _QUEUE.removeElementAt(0);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
            _QUEUE.notifyAll();
        }
        if (millis != 1000) {
            millis = 1000;
        }
        updateTimeCounter();
        if (workFlowbuf.length() > 0) {
            millis = processQueue(workFlowbuf, millis);
            workFlowbuf.delete(0, workFlowbuf.length());
        }

        synchronized (_MESSAGES) {

            if (!_MESSAGES.isEmpty()) {
                msg = _MESSAGES.firstElement();
                _MESSAGES.removeElementAt(0);
            }
            messagescount = _MESSAGES.size();
            _MESSAGES.notifyAll();
        }
        if (messagescount > 0) {
            System.out.println("[Terminal] Nuber of messages in Terminal queue: " + messagescount);
        }
        if (msg != null) {
            if (_FAR_WAYS != null) {
                _FAR_WAYS.add_Message(msg);
            }
        }
        msg = null;

        checkThreads();
    }
    
    /**
     *
     */
    public void fill() {
        //всякая инициализация
        updateTimeCounter();
        System.out.println("[Terminal] Init TERM");
        System.out.println("[Terminal] Set _MODE to INTERNAL_BLOCKED_MODE");
        _MODE = CommunicationManager.INTERNAL_BLOCKED_MODE;
        //Object mut = new Object();
        //System.out.println("Version: " + _VERSION);//
        if (_PLATFORM == _PLATFORM_UNDEFINED) {
            get_Platform();
        }

        updateTimeCounter();
        prepareAudio();//Подготовка к управлению аудиокодеком
        updateTimeCounter();
        clearFS();//Очистка FS
        updateTimeCounter();
        Task ts2 = new Task(this, new RstGsmEvent(), "RstGsm");
        updateTimeCounter();
        if (_TIMER_GSMRST != null) {
            _TIMER_GSMRST.cancel();
            _TIMER_GSMRST = null;
        }
        if (_TIMER_GSMRST == null) {
            _TIMER_GSMRST = new Timer();
        }
        updateTimeCounter();
        _TIMER_GSMRST.schedule(ts2, 24 * 60 * 60 * 1000, 24 * 60 * 60 * 1000);
        updateTimeCounter();
        //initAT();//Инициализация АТ
        int objid = 0;

        //Получение CID
//        try {
//
//            _CID = _MONITOR.getCID();
//        } catch (IllegalStateException ex) {
//        } catch (NumberFormatException ex) {
//        } catch (Exception ex) {
//        } catch (Error ex) {
//        }
        try {
            if (_INDICATION == null) {
                _INDICATION = new Indicator();
            }
            _INDICATION.set_Handler(this);
            _INDICATION.binkOn(work_led);
            _INDICATION.init();
            updateTimeCounter();
            try {
                _INDICATION.start();
//                try {
//                    Thread.sleep(3000);
//                } catch (InterruptedException ex) {
//                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }

        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
        }

        objid = createConfig();
        updateTimeCounter();
        try {
            setJavaNetSettings();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        updateTimeCounter();

        _PacketHandler.start();

        _FAR_WAYS.set_Mode(_MODE);
        _FAR_WAYS.set_Communication(this);
        _FAR_WAYS.set_Remote(_REMOTE_CONTROL_LIST);
        _FAR_WAYS.init();

//        _ATC.send("AT^SIND=\"rssi\",2\r", (ATCommandResponseListener) this);
//        try {
//            
//            ATResponse(_ATC.send("AT^SIND=\"rssi\",2\r"));
//        } catch (ATCommandFailedException ex) {
//            ex.printStackTrace();
//        } catch (IllegalStateException ex) {
//            ex.printStackTrace();
//        } catch (IllegalArgumentException ex) {
//            ex.printStackTrace();
//        }
        _FAR_WAYS.begin();        
        _COM.start();
        _COM.set_Mode(_MODE);
        _PacketHandler.set_Mode(_MODE);
        beep();
        updateTimeCounter();
        System.out.println("[Terminal] End of init()");
    }

    private void initAT() {
        Vector commands = null;
        //#ifdef BGS5Autostart
//#         commands = configs.bgs5.InitATCommands.Commands;
        //#elifdef BGS5Debug
//#         commands = configs.bgs5.InitATCommands.Commands;
        //#elifdef BGS5AutostartOldInd
//#         commands = configs.bgs5.InitATCommands.Commands;
        //#elifdef BGS5DebugOldInd
//#         commands = configs.bgs5.InitATCommands.Commands;
        //#elifdef EHS5Autostart
//#         commands = configs.ehs5.InitATCommands.Commands;
        //#elifdef EHS5AutostartOldInd
//#         commands = configs.ehs5.InitATCommands.Commands;
        //#elifdef EHS5Debug
//#         commands = configs.ehs5.InitATCommands.Commands;
        //#elifdef EHS5DebugOldInd
//#         commands = configs.ehs5.InitATCommands.Commands;
        //#elifdef TC65Gpio
//#         commands = configs.tc65.InitATCommands.Commands;
        //#elifdef TC65Spi
//#         commands = configs.tc65.InitATCommands.Commands;
        //#elifdef TC65SpiConcept
//#         commands = configs.tc65.InitATCommands.Commands;
        //#endif

        _ATC.send("ATI\r", this);
        synchronized (_MUTEX) {
            try {
                _MUTEX.wait(3000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            _MUTEX.notifyAll();
        }
        Enumeration cm = commands.elements();
        System.out.println("Initialize terminal for AT commands");
        while (cm.hasMoreElements()) {
            updateTimeCounter();
            try {
                _ATC.send(((String) cm.nextElement() + "\r"));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            synchronized(_MUTEX){
                try {
                    _MUTEX.wait(100);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                _MUTEX.notifyAll();
            }
        }

//        _ATC.send("AT\r");
//        _ATC.send("AT+IPR=115200\r");
//        _ATC.send("ATE0\r");
//        _ATC.send("AT+CMER=3,0,0,2\r");
//        _ATC.send("AT+CFUN=1,0\r");
//        //at^sjotap=,,,,,,,,,,,"off","off"
//        _ATC.send("at^sjotap=,,,,,,,,,,,\"off\",\"off\"\r");
////        _ATC.send("AT^SNFS=4\r");
//        _ATC.send("AT^SJOTAP=,,,,,,,,,,,,\r");
//        _ATC.send("AT^SCFG=\"Radio/FirstScan\",\"1\"\r\r");
//        _ATC.send("AT^SCFG=\"Radio/CNS\",\"1\"\r");
//        _ATC.send("AT+CGSN\r");
//        _ATC.send("AT+CSMS=1\r");
//        _ATC.send("AT+CMGF=1\r");
//        _ATC.send("AT+CNMI=1,1,0,2,1\r");
//        _ATC.send("at^scfg=\"Gpio/mode/SPI\",\"std\"\r");
//        _ATC.send("at^scfg=\"Gpio/mode/SYNC\",\"std\"\r");
//        _ATC.send("at^scfg=\"Gpio/mode/DAI\",\"std\"\r");
//        _ATC.send("AT^SNFS=4\r");
//        _ATC.send("AT^SNFO=60,0\r");
//        _ATC.send("AT^SNFI=80\r");
//        _ATC.send("AT^SRTC=1,0\r");
////        _ATC.send("AT^SRTC=1,4\r");
//        _ATC.send("AT^SLED=2\r");
//        _ATC.send("AT+CPMS=\"SM\",\"SM\",\"SM\"\r");
//        _ATC.send("AT+CR=1\r");
//        _ATC.send("AT+CRC=1\r");
//        _ATC.send("AT^SPOW=1,0,0\r");
//        _ATC.send("AT^SCFG=\"Serial/Interface/Allocation\",\"2\"\r");
//        //_ATC.send("at^scfg=\"Userware/Stdout\",\"usb1\",,,,\"off\"\r");
//        _ATC.send("at^scfg=\"Userware/Stdout\",\"usb1\",,,,\"off\"\r");
//        _ATC.send("at^scfg=\"Tcp/OT\",\"6\"\r");
//        //#ifdef EHS        
////#         _ATC.send("AT^SXRAT=1,0\r");
////#        _ATC.send("AT^SPOW=1,0,0\r");
//        //#endif
//        
//
////^SIND: service,0,1
////^SIND: message,0,0
////^SIND: call,0,0
////^SIND: roam,0,0
////^SIND: smsfull,0,0
////^SIND: rssi,0,3
////^SIND: ciphcall,0,0
////^SIND: simdata,0
////^SIND: eons,0,4,"MOTIV","MOTIV"
////^SIND: nitz,0,"15/11/20,06:43:03",+20,0
////^SIND: psinfo,0,2
////^SIND: pacsp,0,99
////^SIND: simtray,0,1
////^SIND: lsta,0,0        
//        
//        //#ifdef TC65Spi        
//        System.out.println("AT+CIND: " + _ATC.send("AT+CIND=1,1,1,0,1,1,1,1,1\r"));
//        System.out.println("AT+CMER: " + _ATC.send("AT+CMER=2,0,0,2\r"));
//        _ATC.send("AT^SNFO=1,4096,5792,8192,11584,16384,4,0\r");//1,16384,16384,16384,16384,16384,4,0
//        _ATC.send("AT^SNFI=7,32767\r");
//        _ATC.send("AT^SNFW\r");
//        _ATC.send("AT^SCFG=\"Userware/Stdout\",\"USB\"\r");
//        //#elifdef TC65Gpio
////#         System.out.println("AT+CIND: " + _ATC.send("AT+CIND=1,1,1,0,1,1,1,1,1\r"));
////#         System.out.println("AT+CMER: " + _ATC.send("AT+CMER=2,0,0,2\r"));
////#         _ATC.send("AT^SNFO=1,4096,5792,8192,11584,16384,4,0\r");//1,16384,16384,16384,16384,16384,4,0
////#         _ATC.send("AT^SNFI=7,32767\r");
////#         _ATC.send("AT^SNFW\r");
////#         _ATC.send("AT^SCFG=\"Userware/Stdout\",\"USB\"\r");
//        //#endif
//        _ATC.send("AT^SIND=service,1\r");
//        _ATC.send("AT^SIND=message,1\r");
//        _ATC.send("AT^SIND=call,1\r");
//        _ATC.send("AT^SIND=roam,1\r");
//        _ATC.send("AT^SIND=smsfull,1\r");
//        _ATC.send("AT^SIND=rssi,1\r");
//        _ATC.send("AT^SIND=ciphcall,1\r");
//        _ATC.send("AT^SIND=simdata,1\r");
//        _ATC.send("AT^SIND=eons,1\r");
//        _ATC.send("AT^SIND=nitz,1\r");
//        _ATC.send("AT^SIND=psinfo,1\r");
//        _ATC.send("AT^SIND=pacsp,1\r");
//        _ATC.send("AT^SIND=simtray,1\r");
//        _ATC.send("AT^SIND=lsta,1\r");        
//        _ATC.send("AT+CPMS=\"SM\",\"SM\",\"SM\"\r");
//
////        _ATC.send("AT+FUN=1,0\r");
//        _ATC.send("AT+CREG=2\r");
//        _ATC.send("AT+COPS=0\r");
////        _ATC.send("\r");
////        _ATC.send("\r");
    }

    private void updateTimeCounter() {
        _LOOP_IT = System.currentTimeMillis();
    }

    private JSONObject loadConfig() {
        JSONObject config = null;
        InputStream isr;
        byte[] buf = null;
        try {
            FileConnection fconn = (FileConnection) Connector.open("file:///" + _WorkDir + "/config.cfg", Connector.READ);
            if (!fconn.exists()) {
                fconn.close();
                return null;
            }
            isr = fconn.openInputStream();
            buf = new byte[isr.available()];
            int readed = isr.read(buf);
            isr.close();
            fconn.close();
            if (readed == 0) {
                return null;
            }
            config = new JSONObject(new String(buf));
        } catch (IOException ioe) {
        } catch (JSONException ex) {
        }
        return config;
    }

    private JSONObject loadDefaultConfig() {
        JSONObject config = null;
        InputStream is = null;
        try {
            is = System.class.getResourceAsStream("/configs/default.json");
            byte[] buf = new byte[is.available()];
            int readed = is.read(buf);
            is.close();
            if (readed <= 0) {
                return null;
            }
            config = new JSONObject(new String(buf));
        } catch (IOException ioe) {
        } catch (JSONException ex) {
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ex) {
                }
            }
        }
        return config;
    }

    private JSONObject loadTestConfig() {
        JSONObject config = null;
        InputStream is = null;
        try {
            is = System.class.getResourceAsStream("/configs/test.json");
            byte[] buf = new byte[is.available()];
            if (is.read(buf) <= 0) {
                is.close();
                return null;
            }
            config = new JSONObject(new String(buf));
        } catch (IOException ioe) {
        } catch (JSONException ex) {
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ex) {
                }
            }
        }
        return config;
    }

    private void UpdCfg(int aknumber, String baseurl){
        System.out.println("Obtain config for " + aknumber);
        JSONObject cfg = null;
        _DOWNLOADER = new Downloader(this);
        System.out.println("Try download: " + "http://" + baseurl + "configs/oko3c/" + aknumber + "/config.cfg");
        _DOWNLOADER.set_Source("http://" + baseurl + "configs/oko3c/" + aknumber + "/config.cfg");
        _DOWNLOADER.set_Destination(_WorkDir + "/config.cfg");
        if(_DOWNLOADER.invoke_download()){
            
        }
    }
    private JSONObject obtainConfig(int aknumber, String baseurl) {
        System.out.println("Obtain config for " + aknumber);
        JSONObject cfg = null;
        Downloader dw = new Downloader( this);
        System.out.println("Try download: " + "http://" + baseurl + "configs/oko3c/" + aknumber + "/config.cfg");
        dw.set_Source("http://" + baseurl + "configs/oko3c/" + aknumber + "/config.cfg");
        dw.set_Destination(_WorkDir + "/config.cfg");

        if (dw.download()) {
            cfg = loadConfig();
        }
        if (cfg != null) {
            System.out.println("Config is obtained.");
        } else {
            System.out.println("Config is null.");
        }
        return cfg;
    }

    private boolean acceptConfig(JSONObject config, boolean sendlive) {
        boolean resault = true;
        StringBuffer strbuf = new StringBuffer();
        System.out.println("Accept Config");
        ///----------------------------------------------------config!!!!!------------------------------------------------------------
        _FAR_WAYS.pause();
        //<editor-fold defaultstate="collapsed" desc="Загрузка конфига">
        JSONObject syst = config.optJSONObject("System");
        String spassword = syst.optString("password", "");
        int objid = syst.optInt("objectID", 0);
        this._ObjectID = objid;
        _FAR_WAYS.set_TinetAddr(objid);
        int lfp = syst.optInt("lifeperiod", 0);
        _LIVE_PERIOD = lfp;
        this._CALL_TIME = syst.optInt("calltime", 60);
        String mod = syst.optString("mode", "");
        System.out.println(spassword + " " + objid + " " + lfp + " " + mod);
        System.out.println("---Load mode state---");
        try {
            try {
                _MODE = loadMODE();
                _FAR_WAYS.set_Mode(_MODE);
                _COM.set_Mode(_MODE);
                _PacketHandler.set_Mode(_MODE);
                System.out.println("---Mode state loaded---");
            } catch (Exception ex) {
                System.out.println("---Mode state loaded fail---");
            }
            if (_MODE == 0) {
                if (mod.toUpperCase().indexOf("NORMAL") >= 0) {
                    this._MODE = CommunicationManager.N0RMAL_MODE;
                } else if (mod.toUpperCase().indexOf("BLOCKED") >= 0) {
                    this._MODE = CommunicationManager.EXTERNAL_BLOCKED_MODE;

                } else if (mod.toUpperCase().indexOf("SAFE") >= 0) {
                    this._MODE = CommunicationManager.SAFE_MODE0;
                }
            }
            saveMODE();
            _OLD_MODE = _MODE;
            switch (_MODE) {
                case CommunicationManager.INTERNAL_BLOCKED_MODE:

                    //#ifdef EHS5AutostartOldInd
//#                     _INDICATION.setLed(work_led, Indicator.GREEN);
//#                     _INDICATION.set_BlinkSpeed(Indicator.HI_SPEED);
                    //#elifdef EHS5DebugOldInd
//#                     _INDICATION.setLed(work_led, Indicator.GREEN);
//#                 _INDICATION.set_BlinkSpeed(Indicator.HI_SPEED);
                    //#elifdef BGS5AutostartOldInd
//#                     _INDICATION.setLed(work_led, Indicator.GREEN);
//#                     _INDICATION.set_BlinkSpeed(Indicator.HI_SPEED);
                    //#elifdef BGS5DebugOldInd
//#                     _INDICATION.setLed(work_led, Indicator.GREEN);
//#                     _INDICATION.set_BlinkSpeed(Indicator.HI_SPEED);
                    //#else
                    _INDICATION.setLed(work_led, Indicator.RED);
                    //#endif                        
//                        _INDICATION.setLed(work_led, Indicator.RED);
                    break;
                case CommunicationManager.EXTERNAL_BLOCKED_MODE:
                    //#ifdef EHS5AutostartOldInd
//#                     _INDICATION.setLed(work_led, Indicator.GREEN);
//#                     _INDICATION.set_BlinkSpeed(Indicator.HI_SPEED);
                    //#elifdef EHS5DebugOldInd
//#                     _INDICATION.setLed(work_led, Indicator.GREEN);
//#                 _INDICATION.set_BlinkSpeed(Indicator.HI_SPEED);
                    //#elifdef BGS5AutostartOldInd
//#                     _INDICATION.setLed(work_led, Indicator.GREEN);
//#                     _INDICATION.set_BlinkSpeed(Indicator.HI_SPEED);
                    //#elifdef BGS5DebugOldInd
//#                     _INDICATION.setLed(work_led, Indicator.GREEN);
//#                     _INDICATION.set_BlinkSpeed(Indicator.HI_SPEED);
                    //#elifdef TC65Gpio
//#                 _INDICATION.set_BlinkSpeed(Indicator.HI_SPEED);                    
                    //#else
                    _INDICATION.setLed(work_led, Indicator.RED);
                    //#endif                        
//                        _INDICATION.setLed(work_led, Indicator.RED);
                    break;
                case CommunicationManager.N0RMAL_MODE:
                    //#ifdef EHS5AutostartOldInd
//#                     _INDICATION.setLed(work_led, Indicator.GREEN);
//#                     _INDICATION.set_BlinkSpeed(Indicator.SLO_SPEED);
                    //#elifdef EHS5DebugOldInd
//#                     _INDICATION.setLed(work_led, Indicator.GREEN);
//#                 _INDICATION.set_BlinkSpeed(Indicator.SLO_SPEED);
                    //#elifdef BGS5AutostartOldInd
//#                     _INDICATION.setLed(work_led, Indicator.GREEN);
//#                     _INDICATION.set_BlinkSpeed(Indicator.SLO_SPEED);
                    //#elifdef BGS5DebugOldInd
//#                     _INDICATION.setLed(work_led, Indicator.GREEN);
//#                     _INDICATION.set_BlinkSpeed(Indicator.SLO_SPEED);
                    //#elifdef TC65Gpio
//#                 _INDICATION.set_BlinkSpeed(Indicator.SLO_SPEED);                    
                    //#else
                    _INDICATION.setLed(work_led, Indicator.GREEN);
                    //#endif                        
//                        _INDICATION.setLed(work_led, Indicator.GREEN);
                    break;
                case CommunicationManager.SAFE_MODE0:
                    break;
            }
        } catch (Exception ex) {
            System.out.println("AcceptConfig-LoadConfig");
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Настройка конечных точек">
        try {
            strbuf.delete(0, strbuf.length());
            JSONArray pts = config.optJSONArray("Points");
            if (pts != null && pts.length() > 0) {
                _FAR_WAYS.clear();
                System.out.println("Size of Points in config is " + pts.length());
                for (int i = 0; i < pts.length(); i++) {
                    String name = pts.optJSONObject(i).optString("name", "");
                    String pass = pts.optJSONObject(i).optString("pass", "");
                    boolean state = pts.optJSONObject(i).optBoolean("state");
                    String mods = pts.optJSONObject(i).optString("modes", "");

                    strbuf.append("Found point of index ");
                    strbuf.append(i);
                    strbuf.append("\r\n");

                    strbuf.append("name ");
                    strbuf.append(name);
                    strbuf.append("\r\n");

                    strbuf.append("pass ");
                    strbuf.append(pass);
                    strbuf.append("\r\n");

                    strbuf.append("state ");
                    strbuf.append(state);
                    strbuf.append("\r\n");

                    strbuf.append("modes ");
                    strbuf.append(mods);
                    strbuf.append("\r\n");

                    System.out.println(strbuf.toString());
                    strbuf.delete(0, strbuf.length());
                    if (!state) {
                        continue;
                    }
                    Point pt = new Point(name);
                    pt.set_Pass(pass);

                    if (mods.toUpperCase().indexOf("NORMAL") >= 0) {
                        pt.set_Modes(CommunicationManager.N0RMAL_MODE, (char) 0x01);
                    }
                    if (mods.toUpperCase().indexOf("SAFE") >= 0) {
                        pt.set_Modes(CommunicationManager.SAFE_MODE0, (char) 0x01);
                    }
                    if (mods.toUpperCase().indexOf("BLOCKED") >= 0) {

                        pt.set_Modes(CommunicationManager.INTERNAL_BLOCKED_MODE, (char) 0x01);
                    }

                    JSONArray chnls = pts.optJSONObject(i).optJSONArray("channels");
                    for (int y = 0; y < chnls.length(); y++) {
                        String chname = chnls.optJSONObject(y).optString("name", "");
                        String churl = chnls.optJSONObject(y).optString("url", "");
                        boolean chstate = chnls.optJSONObject(y).optBoolean("state");
                        String chfiltype = chnls.optJSONObject(y).optJSONObject("filter").optString("type", "");
                        String chfilval = chnls.optJSONObject(y).optJSONObject("filter").optString("value", "");
                        AkMessageFilter f = new AkMessageFilter();

                        if (chfiltype.compareTo("") == 0) {
                            f.set_Type(AkMessageFilter.OFF);
                        } else if (chfiltype.toLowerCase().indexOf("allow") >= 0) {
                            f.set_Type(AkMessageFilter.ALLOW);
                        } else if (chfiltype.toLowerCase().indexOf("deny") >= 0) {
                            f.set_Type(AkMessageFilter.DENY);
                        } else if (chfiltype.toLowerCase().indexOf("off") >= 0) {
                            f.set_Type(AkMessageFilter.OFF);
                        }
                        String[] toks = Parser.Parse(chfilval, ',');
                        for (int z = 0; z < toks.length; z++) {
                            f.add_inFilter(toks[z]);
                        }
                        if (chname.toUpperCase().indexOf("TINET") >= 0 && chstate) {

                            pt.set_Channels(Point.TINET_CHANNEL, (char) 0x01);
                            pt.set_Tinet(churl);
                            pt.set_TinetFilter(f);

                        } else if (chname.toUpperCase().indexOf("SMS") >= 0 && chstate) {

                            pt.set_Channels(Point.SMS_CHANNEL, (char) 0x01);
                            pt.set_SMS(churl);
                            pt.set_SMSFilter(f);

                        }

                        System.out.println("chname " + chname);
                        System.out.println("churl " + churl);
                        System.out.println("chstate " + chstate);
                        System.out.println("filter type " + chfiltype);
                        System.out.println("filter value " + chfilval);
                    }
//            JSONArray chns=pts.getJSONArray(i)
//
//            
//            System.out.println("url "+pts.optJSONObject(i).optString("url", ""));
//            System.out.println("filter type "+pts.optJSONObject(i).optJSONObject("filter").optString("type", ""));
//            System.out.println("filter type "+pts.optJSONObject(i).optJSONObject("filter").optString("value", ""));

                    _FAR_WAYS.add_Point(pt);

                }
            }

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception ex) {
            System.out.println("AcceptConfig-EndPoints");
        }

        //</editor-fold>
        ///---------------------------------------------------------------------------------------------------------------------------
        //<editor-fold defaultstate="collapsed" desc="Настройка списков телефонов разрешённых для входящих звонков и удалённого управления">
        try {
            String[] remote = Parser.GetTokenList(config.optString("RemoteControl"));
            String[] ws = Parser.GetTokenList(config.optString("WiteList"));
            String[] sos = Parser.GetTokenList(config.optString("SOSphones"));
            String[] rByCall = Parser.GetTokenList(config.optString("RestartByCall"));
            for (int i = 0; i < rByCall.length; i++) {
                _RESTART_BY_CALL_LIST.addElement(rByCall[i]);
                System.out.println("rByCall[" + i + "]: " + _RESTART_BY_CALL_LIST.elementAt(i));
            }
            for (int i = 0; i < ws.length; i++) {
                _WHITE_LIST.addElement(ws[i]);
                System.out.println("witeList[" + i + "]: " + _WHITE_LIST.elementAt(i));
            }
            for (int i = 0; i < remote.length; i++) {
                _REMOTE_CONTROL_LIST.addElement(remote[i]);
                System.out.println("Remote[" + i + "]: " + _REMOTE_CONTROL_LIST.elementAt(i));
            }
            _FAR_WAYS.set_Remote(_REMOTE_CONTROL_LIST);
            for (int i = 0; i < sos.length; i++) {
                _SOS_LIST.addElement(sos[i]);
                System.out.println("SOS[" + i + "]: " + _SOS_LIST.elementAt(i));
            }
            //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Настройка списков телефонов для прямой связи">
            String[] callto = Parser.GetTokenList(config.optString("EmergencyCallTo"));
            System.out.println("Try to sets help phone numbers!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

            String buf = "";
            Vector phs = new Vector(0);
            Vector newphs = new Vector(0);
            //Загрузка текущих номеров.
            System.out.println("Get current numbers!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

            _ATC.send("AT+CPBS=\"SM\"\r", this);
            for (int i = 1; i < 4; i++) {
                _ATC.send("AT+CPBR=" + i + "\r", this);
                phs.addElement(buf.trim());
            }

            if (callto == null) {
                newphs.addElement("0");
                newphs.addElement("0");
                newphs.addElement("0");
            } else if (callto.length == 3) {
                newphs.addElement(callto[0]);
                newphs.addElement(callto[1]);
                newphs.addElement(callto[2]);
            } else if (callto.length == 2) {
                newphs.addElement(callto[0]);
                newphs.addElement(callto[1]);
                newphs.addElement("0");
            } else if (callto.length == 1) {
                newphs.addElement(callto[0]);
                newphs.addElement("0");
                newphs.addElement("0");
            } else if (callto.length == 0) {
                newphs.addElement("0");
                newphs.addElement("0");
                newphs.addElement("0");
            }
            System.out.println("Write numbers in to SIM!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            String oldph = "";
            String newph = "";
            String r = "";
            for (int i = 0; i < 3; i++) {
                System.out.println("Old: " + (String) _CALL_TO_LIST.elementAt(i));
                System.out.println("New: " + (String) newphs.elementAt(i));
                if (((String) phs.elementAt(i)).indexOf((String) newphs.elementAt(i)) == -1) {

                    System.out.println("Delete old number");
                    _ATC.send("AT+CPBW=" + (i + 1) + '\r', this);
                    System.out.println(r);
                    String cmd = "AT+CPBW=" + (i + 1) + ",\"" + (String) newphs.elementAt(i) + "\",,\"HELP" + (i + 1) + "\"" + '\r';
                    System.out.println("Write new number: " + cmd);
                    _ATC.send(cmd, this);

                }
            }
        } catch (Exception ex) {
            System.out.println("AcceptConfig-Phones");
        }
        //</editor-fold>                

        //<editor-fold defaultstate="collapsed" desc="Настройка заблокированных зон оповещения">
        try {
            System.out.println("Set blocked zones");
            String blz = config.optString("BlockedZones");
            System.out.println(blz);
            if (this._INDICATION != null) {
                String[] bz = Parser.GetTokenList(blz);
                byte[] bbz = new byte[bz.length];
                for (int i = 0; i < bz.length; i++) {
                    bbz[i] = Byte.parseByte(bz[i]);
                }
                //#ifdef EHS5AutostartOldInd
//#                 _INDICATION.setBlockedZones(new byte[]{1, 2, 3, 4, 5, 6});
                //#elifdef EHS5DebugOldInd
//#             _INDICATION.setBlockedZones(new byte[]{1, 2, 3, 4, 5, 6});
                //#elifdef BGS5AutostartOldInd
//#                 _INDICATION.setBlockedZones(new byte[]{1, 2, 3, 4, 5, 6});
                //#elifdef BGS5DebugOldInd
//#                 _INDICATION.setBlockedZones(new byte[]{1, 2, 3, 4, 5, 6});
                //#elifdef TC65Gpio
//#                 _INDICATION.setBlockedZones(new byte[]{1, 2, 3, 4, 5, 6});
                //#else
                _INDICATION.setBlockedZones(bbz);
                //#endif

                switch (_MODE) {
                    case CommunicationManager.EXTERNAL_BLOCKED_MODE:
                        for (byte i = 2; i < 8; i++) {
                            _INDICATION.setLed(i, Indicator.OFF);
                        }
                        break;
                    default:
                        for (byte i = 2; i < 8; i++) {
                            _INDICATION.setLed(i, Indicator.GREEN);
                        }
                        break;
                }
            } else {
                System.out.println("_INDICATION is NULL in Terminal");
            }
        } catch (Exception ex) {
            System.out.println("AcceptConfig-BlockedZones");
        }
        //</editor-fold> 

        //<editor-fold defaultstate="collapsed" desc="Проверка смены sim карты"> 
        try {
            this._SPY_NUMBER = config.optString("SIMSpy");
            System.out.println("SPYSim: " + this._SPY_NUMBER);
            String cid = loadCID();
            System.out.println("Loaded cid: " + cid);

            if (_CID.compareTo(cid) != 0) {
                saveCID(_CID);
                SMSMessage cidsms = new SMSMessage();
                if (this._SPY_NUMBER.compareTo("") != 0) {
                    cidsms.Phone = this._SPY_NUMBER;
                    cidsms.Text = "9:001-301-" + _ObjectID;
                    synchronized (_MESSAGES) {
                        _MESSAGES.addElement(cidsms);
                        _MESSAGES.notifyAll();
                    }
                }
            }
        } catch (Exception ex) {
            System.out.println("AcceptConfig-CheckSim");
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Настройка таймера контрольного периода">     
        if (sendlive) {
            if (_MODE != CommunicationManager.EXTERNAL_BLOCKED_MODE) {
                try {
////                Task ts = ;
                    if (_TIMER_LIVE != null) {
                        _TIMER_LIVE.cancel();
                        _TIMER_LIVE = null;
                    }
                    if (_TIMER_LIVE == null) {
                        _TIMER_LIVE = new Timer();
                    }
//                _TIMER_LIVE.schedule(new Task(this, new LiveEvent(), "Live"), 20000, _LIVE_PERIOD * 60 * 60 * 1000);//часы*минуты*секунды*миллисекунды
                    _TIMER_LIVE.schedule(new Task(this, new LiveEvent(), "Live"), _LIVE_PERIOD * 60 * 60 * 1000, _LIVE_PERIOD * 60 * 60 * 1000);//часы*минуты*секунды*миллисекунды

                } catch (Exception ex) {
                    System.out.println("AcceptConfig-LivePeriod");
                } catch (Error err) {
                }
            }

        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Настройка таймаута для соединения с  сервером">
        int tcptmout= config.optInt("tcptimeout");
        if (tcptmout ==0){
            tcptmout=30;
        }
        _FAR_WAYS.set_ConnectTimeout(tcptmout);
        //</editor-fold>
        
        _FAR_WAYS.resume();
        return resault;
    }

    /**
     *
     * @return
     */
    public boolean begin() {

        /**
         * "Mary Had A Little Lamb" has "ABAC" structure. Use block to repeat
         * "A" section.
         */
//        byte tempo = 30; // set tempo to 120 bpm
//        byte d = 8;      // eighth-note
//
//        byte C4 = ToneControl.C4;;
//        byte D4 = (byte) (C4 + 2); // a whole step
//        byte E4 = (byte) (C4 + 4); // a major third
//        byte G4 = (byte) (C4 + 7); // a fifth
//        byte rest = ToneControl.SILENCE; // rest
//
//        byte[] mySequence = {
//            ToneControl.VERSION, 1, // version 1
//            ToneControl.TEMPO, tempo, // set tempo
//            ToneControl.BLOCK_START, 0, // begin define "A" section
//            E4, d, D4, d, C4, d, E4, d, // content of "A" section
//            E4, d, E4, d, E4, d, rest, d,
//            ToneControl.BLOCK_END, 0, // end define "A" section
//            ToneControl.PLAY_BLOCK, 0, // play "A" section
//            D4, d, D4, d, D4, d, rest, d, // play "B" section
//            E4, d, G4, d, G4, d, rest, d,
//            ToneControl.PLAY_BLOCK, 0, // repeat "A" section
//            D4, d, D4, d, E4, d, D4, d, C4, d // play "C" section
//        };
//
//        try {
//            Player p = Manager.createPlayer(Manager.TONE_DEVICE_LOCATOR);
//            p.realize();
//            ToneControl c = (ToneControl) p.getControl("ToneControl");
//            c.setSequence(mySequence);
//            p.begin();
//        } catch (IOException ioe) {
//        } catch (MediaException me) {
//        }
        _ATC.begin();
        updateTimeCounter();
        if (!_FLAG) {
            _FLAG = true;
        }
        if (_WORK == null) {
            _WORK = new Thread(this, "TERMINAL");
        }
        try {
            _WORK.start();
//        _COORDINATOR.begin();
        } catch (IllegalThreadStateException ex) {
            _FLAG = false;
            _WORK = null;
            return false;
        }
//        _ATC.send("ATE1\r");
//        _ATC.send("AT\r", this);
        return true;
    }

    /**
     *
     * @return
     */
    public boolean end() {
        if (!_FLAG) {
            return true;
        }
        _FLAG = false;
//        _COORDINATOR.end();
        try {
            synchronized (_QUEUE) {
                _QUEUE.notifyAll();
            }
        } catch (IllegalMonitorStateException ex) {

        }
        _INDICATION.offAllLeds();
        _INDICATION.stop();

        _COM.stop();
        _PacketHandler.stop();
        _FAR_WAYS.end();

        try {
            _WORK.join();
            _WORK = null;
        } catch (InterruptedException ex) {
            return false;

        }
        return true;
    }

    /**
     *
     */
    public void interrupt() {
//        Object m = new Object();
        _FLAG = false;
        try {
            _WORK.interrupt();
        } catch (NullPointerException ex) {

        }
        _WORK = null;
//        try {
//            synchronized (m) {
//                m.wait(3000);
//            }
//        } catch (InterruptedException ex) {
//            ex.printStackTrace();
//        }
//        try {
//            _ATC.release();
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        } catch (IllegalStateException ex) {
//            ex.printStackTrace();
//        }
    }

    /**
     *
     */
    public void emerg_int() {
        this.interrupt();
        realse();
        _INDICATION.emerg_int();
        _COM.emerg_int();
        _PacketHandler.emerg_int();
        _FAR_WAYS.emerg_int();
    }

    /**
     *
     */
    public void realse() {
        _ATC.removeListener(this);
//        try {
//            _ATC.release();
//        } 
////#ifdef TC65Spi
////#         catch (ATCommandFailedException ex) {
////#             ex.printStackTrace();
////#         } 
////#elifdef TC65SpiDebug
////#         catch (ATCommandFailedException ex) {
////#             ex.printStackTrace();
////#         } 
////#elifdef TC65SpiConcept
////#         catch (ATCommandFailedException ex) {
////#             ex.printStackTrace();
////#         } 
////#elifdef TC65SpiConceptDebug
////#         catch (ATCommandFailedException ex) {
////#             ex.printStackTrace();
////#         } 
////#elifdef TC65Gpio
////#         catch (ATCommandFailedException ex) {
////#         } 
////#elifdef TC65GpioDebug
////#         catch (ATCommandFailedException ex) {
////#         } 
////#elifdef EHS5Autostart
////#         catch (IOException ex) {
////#             ex.printStackTrace();
////#         } 
////#elifdef BGS5Autostart
//        catch (IOException ex) {
//            ex.printStackTrace();
//        } 
////#elifdef BGS5AutostartOldInd
////#         catch (IOException ex) {
////#             ex.printStackTrace();
////#         } 
////#elifdef EHS5AutostartOldInd
////#         catch (IOException ex) {
////#             ex.printStackTrace();
////#         } 
////#elifdef BGS5Debug
////#         catch (IOException ex) {
////#             ex.printStackTrace();
////#         } 
////#elifdef BGS5DebugOldInd
////#         catch (IOException ex) {
////#             ex.printStackTrace();
////#         } 
////#elifdef EHS5Debug
////#         catch (IOException ex) {
////#             ex.printStackTrace();
////#         } 
////#elifdef EHS5DebugOldInd
////#         catch (IOException ex) {
////#             ex.printStackTrace();
////#         } 
////#endif
//        catch (IllegalStateException ex) {
//        }
    }

    /**
     *
     */
    public void terminate() {
        System.out.println("Terminal: Terminate Terminal thread");
        this.end();
        this.realse();
    }

    /**
     *
     */
    public void restart() {
        System.out.println("Terminal: Restart Terminal thread");
        interrupt();
        begin();

    }

    /**
     *
     */
    public void reboot() {
        System.out.println("Try reboot terminal");
        _ATC.send("AT^SMSO\r", this);
    }

    /**
     *
     */
    public void update() {

        String baseurl = "http://" + _CURRENT_CONFIG.optString("UpdateUrl") + "fw/";
        String opname = "";
        Vector settings = new Vector(0);
        try {
            opname = _CURRENT_OPERATOR_NAME;
            settings = getOpSettings(opname);
        } catch (IllegalStateException ex) {
        } catch (Exception ex) {
        } catch (Error ex) {
        }
        System.out.println("Operator name: " + opname);
        System.out.println("Pause CommunicationManager");

        _FAR_WAYS.pause();
        for (int i = 0; i < ((Vector) settings.elementAt(0)).size(); i++) {
            System.out.println(((Vector) settings.elementAt(0)).elementAt(i).toString());
        }
        String tile = "";
        String appurl = "";

        //#ifdef TC65Gpio
//#             tile="TC23C.jad";
//#             appurl="a:/dist";
        //#elifdef TC65GpioDebug
//#             tile="TC23C.jad";
//#             appurl="a:/dist";            
        //#elifdef TC65Spi
//#             tile="TC23C.jad";
//#             appurl="a:/dist";
        //#elifdef TC65SpiDebug
//#             tile="TC23C.jad";
//#             appurl="a:/dist";
        //#elifdef TC65SpiConcept
//#             tile="TC23C.jad";
//#             appurl="a:/dist";
        //#elifdef TC65SpiConceptDebug
//#             tile="TC23C.jad";
//#             appurl="a:/dist";
//#          
        //#else
        String[] v = Parser.Parse(_VERSION, '.');
        tile = v[0] + '/' + v[1] + "/oko3c.jad";
        appurl = "a:/";
        //#endif

        String updurl = baseurl + tile;
        System.out.println("Update url: " + updurl);
        System.out.println("Generate sjotap command");
        StringBuffer buf = new StringBuffer();
        buf.append("at^sjotap=,\"");
        buf.append(updurl);
        buf.append("\",\"");
        buf.append(appurl);
        buf.append("\",,,\"gprs\",");
        buf.append(((Vector) settings.elementAt(0)).elementAt(0).toString());
        buf.append(',');
        buf.append(((Vector) settings.elementAt(0)).elementAt(1).toString());
        buf.append(',');
        buf.append(((Vector) settings.elementAt(0)).elementAt(2).toString());
        buf.append(',');
        buf.append("\"208.67.222.222\"");
        buf.append(",\"");
        buf.append(baseurl);
        buf.append("aaa.php");
        buf.append("\",\"off\",\"off\"");
        String command = buf.toString();
        String res = "";
//            try {
        //_ATC.send("at^SJOTAP=\"\",\""+url+"\",\"GPRS\",");

        System.out.println(command);
        _ATC.send(command + '\r', this);
        _ATC.send("AT^SJOTAP\r", this);
//                System.out.println(res);
//                if (res.indexOf("ERROR") >= 0) {
//                    _FAR_WAYS.resume();
//                }
//            } catch (ATCommandFailedException ex) {
//            } catch (IllegalStateException ex) {
//            } catch (IllegalArgumentException ex) {
//            }
        _FAR_WAYS.resume();

    }

    private void UpdateCfgState(){
        System.out.println("Try update config");
        String url = _CURRENT_CONFIG.optString("UpdateUrl","firmwares.oko-ek.ru/PU100/");
        String confver = _CURRENT_CONFIG.optString("Version");
        String[] confverstokens = Parser.Parse(confver, '.');
        if(confverstokens.length==2){
            JSONObject defconf = loadDefaultConfig();
            String[] defconfvertokens  = Parser.Parse(defconf.optString("Version"),'.');
            if(defconfvertokens.length==2){
                try{
                    if((Integer.parseInt(defconfvertokens[0])>Integer.parseInt(confverstokens[0]))||(Integer.parseInt(defconfvertokens[1])>Integer.parseInt(confverstokens[1]))){
                       url = defconf.optString("UpdateUrl","firmwares.oko-ek.ru/PU100/");
                       defconf = null;
                       defconfvertokens=null;
                    }
                }catch(NumberFormatException ex){
                    ex.printStackTrace();
                }catch(Exception ex){
                    ex.printStackTrace();
                }
            }
        }
        UpdCfg(_ObjectID, url);
    }
    
    private boolean updateConfig() {
        System.out.println("Try update config");
        String url = _CURRENT_CONFIG.optString("UpdateUrl");
        String confver = _CURRENT_CONFIG.optString("Version");
        String[] confverstokens = Parser.Parse(confver, '.');
        if(confverstokens.length==2){
            JSONObject defconf = loadDefaultConfig();
            String[] defconfvertokens  = Parser.Parse(defconf.optString("Version"),'.');
            if(defconfvertokens.length==2){
                try{
                     int defMajor =Integer.parseInt(defconfvertokens[0]);
                     int defMinor = Integer.parseInt(defconfvertokens[1]);
                     int curMajor = Integer.parseInt(confverstokens[0]);
                     int curMinor = Integer.parseInt(confverstokens[1]);
                    if (defMajor > curMajor) {
                        url = defconf.optString("UpdateUrl");
                        defconf = null;
                        defconfvertokens = null;
                    } else if (defMajor == curMajor) {
                        if (defMinor > curMinor) {
                            url = defconf.optString("UpdateUrl");
                            defconf = null;
                            defconfvertokens = null;
                        }
                    }
                    
                }catch(NumberFormatException ex){
                    ex.printStackTrace();
                }catch(Exception ex){
                    ex.printStackTrace();
                }
            }
        }
        if (url.compareTo("") != 0) {
            JSONObject cfg = obtainConfig(_ObjectID, url);
            if (cfg != null) {
                _CURRENT_CONFIG = cfg;
                _FAR_WAYS.clear();
                if (acceptConfig(_CURRENT_CONFIG, true)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     *
     * @return
     */
    public boolean isALive() {        
        return (System.currentTimeMillis() - _LOOP_IT) <= 60000;
    }

    /**
     *
     */
    public void run() {
        char oldState= _NONE_STATE;
        System.out.println("Terminal: Terminal startted");
        try {
            while (_FLAG) {

                switch (_STATE) {
                    case (_START_STATE):
                        if (oldState != _START_STATE) {
                            _PREV_STATE=oldState;
                            System.out.println("Terminal: START_STATE");
                            oldState = _START_STATE;
                        }
                        updateTimeCounter();
                        startState();
                        break;
                    case (_INIT_AT_STATE):
                        
                        if (oldState != _INIT_AT_STATE) {
                            _PREV_STATE=oldState;
                            oldState = _INIT_AT_STATE;
                            System.out.println("Terminal: _INIT_AT_STATE");
                            initATState();
                        }

                        break;
                    case (_INIT_AT_STATE_PROCESS):
                        if (oldState != _INIT_AT_STATE_PROCESS) {
                            _PREV_STATE=oldState;
                            System.out.println("Terminal: _INIT_AT_STATE_PROCESS");
                            oldState = _INIT_AT_STATE_PROCESS;
                        }
                        break;
                    case (_INIT_STATE):
                        
                        if (oldState != _INIT_STATE) {
                            System.out.println("Terminal: _INIT_STATE");
                            _PREV_STATE=oldState;
                            oldState = _INIT_STATE;                            
                            initState();
                            String[] v = new String[0];
                            try {
                                v = Parser.Parse(_VERSION, '.');
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                            System.out.println("Version size: " + v.length);
                            AkMessage2 m = new AkMessage2();
                            AkMessage2 m2 = new AkMessage2();
                            AkMessage2 m3 = new AkMessage2();
                            m.set_AkNumber(_ObjectID);
                            m.set_EventClass(3);//старт системы
                            m.set_EventCod(5);
                            m.set_PartitionNumber(0);
                            m.set_ZoneNumber(0);
                            m.set_MesNumber(0);
                            System.out.println("SystemStartMessage: " + m.toOko2String());

                            m2 = new AkMessage2();
                            m2.set_AkNumber(_ObjectID);
                            m2.set_MesNumber(get_MessageNumber());
                            m2.set_EventClass(132);//версия прошивки
                            m2.set_EventCod(0);
                            m2.set_PartitionNumber(0);
                            m2.set_ZoneNumber(0);
                            try {

                                //System.out.println(v.toString());
                                switch (v.length) {
                                    case 0:
                                        m2.set_EventCod(0);
                                        m2.set_PartitionNumber(0);
                                        m2.set_ZoneNumber(0);
                                        break;
                                    case 2:
                                        m2.set_EventCod(Integer.parseInt(v[0]));
                                        m2.set_PartitionNumber(Integer.parseInt(v[1]));
                                        m2.set_ZoneNumber(0);
                                        break;
                                    case 3:
                                        m2.set_EventCod(Integer.parseInt(v[0]));
                                        m2.set_PartitionNumber(Integer.parseInt(v[1]));
                                        m2.set_ZoneNumber(Integer.parseInt(v[2]));
                                        break;
                                    default:
                                        m2.set_EventCod(0);
                                        m2.set_PartitionNumber(0);
                                        m2.set_ZoneNumber(0);
                                        break;
                                }

                            } catch (RuntimeException ex) {
                            } catch (Exception ex) {

                            }
                            System.out.println("SystemVersionMessage: " + m2.toOko2String());
                            int sigl = 0;
                            if (_SIGNAL_LEVEL > 100) {
                                sigl = 0x7F;
                            } else {
                                sigl = Util.setBit(_SIGNAL_LEVEL, 7, false);
                            }
                            m3.set_AkNumber(_ObjectID);
                            m3.set_EventClass(3);
                            m3.set_EventCod(21);
                            sigl = Util.setBit(sigl, 7, false);
                            m3.set_ZoneNumber(0);
                            m3.set_PartitionNumber(sigl);
                            m3.set_MesNumber(get_MessageNumber());
                            System.out.println("LiveMessage: " + m3.toOko2String());
                            synchronized (_MESSAGES) {
                                _MESSAGES.addElement(m);
                                _MESSAGES.addElement(m2);
                                _MESSAGES.addElement(m3);
                                _MESSAGES.notifyAll();
                            }
                            _ATC.send(new ATCmdEndAction("END INIT", this));
                        }
                        
//                        _STATE = _WORK_STATE;
                        break;
                    case (_WORK_STATE):
                        if (oldState != _WORK_STATE) {
                            _PREV_STATE=oldState;
                            oldState = _WORK_STATE;
                            if(_DOWNLOADER!=null){
                                _DOWNLOADER=null;
                            }
                            System.out.println("Terminal: _WORK_STATE");
                        }
                        workflow();
                        break;
                    case (_AIRPLAINE_STATE):
                        if(oldState != _AIRPLAINE_STATE){
                            _PREV_STATE=oldState;
                            oldState = _AIRPLAINE_STATE;
                            System.out.println("Terminal: _AIRPLAINE_STATE");
                            disable_AirPlane_Mode();
                        }
                        break;
                    case (_RESTART_GSM_STATE):
                        if (oldState != _RESTART_GSM_STATE) {
                            _COM.restart();
                            _PREV_STATE=oldState;
                            oldState = _RESTART_GSM_STATE;
                            System.out.println("Terminal: _RESTART_GSM_STATE");
                        }
                        restartGSMState();
                        break;
                    case (_UPDATE_FIRMWARE_STATE):
                        if (oldState != _UPDATE_FIRMWARE_STATE) {
                            _PREV_STATE=oldState;
                            oldState = _UPDATE_FIRMWARE_STATE;
                        }
                        update();
                        break;
                    case (_UPDATE_CONFIG_STATE):
                        if (oldState != _UPDATE_CONFIG_STATE) {
                            System.out.println("UPDATE_CONFIG_STATE");
                            _PREV_STATE=oldState;
                            oldState = _UPDATE_CONFIG_STATE;
                            _FILE_UPDATED = false;
                            _FILE_UPDATE_START_TIME=System.currentTimeMillis();
                            UpdateCfgState();
                        }
                        updateTimeCounter();
                        if((System.currentTimeMillis()-_FILE_UPDATE_START_TIME)>60*1000 && !_FILE_UPDATED){
                            reboot();
                        }
                  
                        break;
                    case (_RESTART_BY_CALL_STATE):
                        if (oldState != _RESTART_BY_CALL_STATE) {
                            oldState = _RESTART_BY_CALL_STATE;
                            System.out.println("Terminal: _RESTART_BY_CALL_STATE");
                            _ATC.send("ATH\r");
                            reboot();
                            for (int i = 0; i < 600; i++) {
                                synchronized (_MUTEX) {
                                    _MUTEX.wait(100);
                                    _MUTEX.notifyAll();
                                }
                            }
                        }
                        break;
                    case (_RESTART_STATE):
                        reboot();
                        break;

                }
                synchronized (_MUTEX) {
                    _MUTEX.wait(500);
                    _MUTEX.notifyAll();
                }
            }
        } catch (InterruptedException ex) {
            System.out.println("Terminal: Terminal thread was interrupted");
            _FLAG = false;
            //Завершение потока после прерывания
        }
    }

    private long processQueue(StringBuffer buf, long millis) {
        System.out.println("[Terminal] processQueue: "+buf.toString().trim());
//                    if (buf.toString().indexOf("+PBREADY") >-1) {
//                        _PBREADY = true;
//                        initAT();
//                        if (_CURRENT_CONFIG != null) {
//                            acceptConfig(_CURRENT_CONFIG,false);
//                        }
//
//                    }else if(buf.toString().indexOf("^SYSSTART")>=0){
//                        _SYSSTART=true;
//                        System.out.println("^SYSSTART founded");
//                       buf.delete(0, buf.length());
//                       try{
//                           if(check_AirPlane_Mode()){
//                               disable_AirPlane_Mode();
//                           }else{
//                            _ATC.send("AT+COPS=0\r",this);//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
////                            if(r.indexOf("OK")>=0){
////                                _RESTART_GSM=false;
////                                _FAR_WAYS.resume();
////                            }
//                           }
//                           
//
//                       }catch(Exception ex){
//                           
//                       }
//                        
//                        
//                    }else 
        if (buf.toString().indexOf("NO CARRIER") >= 0) {
            new Timer().schedule(new Task(this, new DisableAudio(), "DisableAdudio"), 5000);
            //NO DIALTONE BUSY NO ANSWER
            //#ifdef TC65Gpio
//#                         _INDICATION.blinkOff(call_led);
//#                         _INDICATION.setLed(call_led, Indicator.GREEN);
            //#elifdef BGS5AutostartOldInd
//#                         _INDICATION.blinkOff(call_led);
//#                         _INDICATION.setLed(call_led, Indicator.GREEN);
            //#elifdef BGS5DebugOldInd
//#                         _INDICATION.blinkOff(call_led);
//#                         _INDICATION.setLed(call_led, Indicator.GREEN);
            //#elifdef EHS5AutostartOldInd
//#                         _INDICATION.blinkOff(call_led);
//#                         _INDICATION.setLed(call_led, Indicator.GREEN);
            //#elifdef EHS5DebugOldInd
//#                         _INDICATION.blinkOff(call_led);
//#                         _INDICATION.setLed(call_led, Indicator.GREEN);                        
            //#elifdef TC65SpiConcept
//#                         _INDICATION.blinkOff(call_led);
//#                         _INDICATION.setLed(call_led, Indicator.GREEN);
            //#elifdef TC65SpiConceptDebug
//#                         _INDICATION.blinkOff(call_led);
//#                         _INDICATION.setLed(call_led, Indicator.GREEN);                        
//#                         
            //#else
            _INDICATION.setLed(call_led, Indicator.ORANGE);
            //#endif

            _CALL_STATE = 0;
            if (this._LAST_CALL != null) {
                if (_LAST_CALL.DIRECTION == 0) {
                    AkMessage2 m = new AkMessage2();
                    m.set_AkNumber(_ObjectID);
                    m.set_MesNumber(get_MessageNumber());
                    m.set_EventClass(9);
                    m.set_EventCod(9);
                    m.set_PartitionNumber(_LAST_CALL.EXTENDED_INDEX);
                    if (_LAST_CALL.STATE == 0) {
                        m.set_ZoneNumber(CommunicationManager.OK);
                    } else {
                        m.set_ZoneNumber(CommunicationManager.NO_CARRIER);
                    }
                    synchronized (_MESSAGES) {
                        _MESSAGES.addElement(m);
                        _MESSAGES.notifyAll();
                    }
                } else if (this._LAST_CALL.DIRECTION == 1) {
                    AkMessage2 m = new AkMessage2();
                    m.set_AkNumber(_ObjectID);
                    m.set_MesNumber(get_MessageNumber());
                    m.set_EventClass(13);
                    m.set_EventCod(2);
                    m.set_PartitionNumber(_LAST_CALL.EXTENDED_INDEX);
                    if (_LAST_CALL.STATE == 0) {
                        m.set_ZoneNumber(CommunicationManager.OK);
                    } else {
                        m.set_ZoneNumber(CommunicationManager.ERROR);
                    }
                    synchronized (_MESSAGES) {
                        _MESSAGES.addElement(m);
                        _MESSAGES.notifyAll();
                    }
                }
                _LAST_CALL = null;
            }
        } else if (buf.toString().indexOf("NO DIALTONE") >= 0) {
            new Timer().schedule(new Task(this, new DisableAudio(), "DisableAdudio"), 5000);
            //#ifdef EHS5DebugOldInd
//#                         _INDICATION.setLed(call_led, Indicator.OFF);
            //#elifdef EHS5AutostartOldInd
//#                         _INDICATION.setLed(call_led, Indicator.OFF);
            //#elifdef BGS5DebugOldInd
//#                         _INDICATION.setLed(call_led, Indicator.OFF);
            //#elifdef BGS5AutostartOldInd
//#                         _INDICATION.setLed(call_led, Indicator.OFF);
            //#elifdef BGS5Autostart
//#             _INDICATION.setLed(call_led, Indicator.RED);
            //#elifdef BGS5Debug
//#                         _INDICATION.setLed(call_led, Indicator.RED);
            //#elifdef EHS5Autostart
//#                          _INDICATION.setLed(call_led, Indicator.RED);
            //#elifdef EHS5Debug
//#                           _INDICATION.setLed(call_led, Indicator.RED);
            //#elifdef TC65Gpio
//#                         _INDICATION.blinkOff(call_led);
//#                         _INDICATION.setLed(call_led, Indicator.GREEN);
            //#elifdef TC65GpioDebug
//#                         _INDICATION.blinkOff(call_led);
//#                         _INDICATION.setLed(call_led, Indicator.GREEN);                        
            //#elifdef TC65SpiConcept
//#                         _INDICATION.blinkOff(call_led);
//#                         _INDICATION.setLed(call_led, Indicator.GREEN);
            //#elifdef TC65SpiConceptDebug
//#                         _INDICATION.blinkOff(call_led);
//#                         _INDICATION.setLed(call_led, Indicator.GREEN);                         
            //#endif                        

            _CALL_STATE = 0;
            if (this._LAST_CALL != null) {
                if (_LAST_CALL.DIRECTION == 0) {
                    AkMessage2 m = new AkMessage2();
                    m.set_AkNumber(_ObjectID);
                    m.set_MesNumber(get_MessageNumber());
                    m.set_EventClass(9);
                    m.set_EventCod(9);
                    m.set_PartitionNumber(_LAST_CALL.EXTENDED_INDEX);
                    m.set_ZoneNumber(CommunicationManager.NO_DIALTONE);
                    synchronized (_MESSAGES) {
                        _MESSAGES.addElement(m);
                        _MESSAGES.notifyAll();
                    }
                }
                _LAST_CALL = null;
            }
        } else if (buf.toString().indexOf("BUSY") >= 0) {
            new Timer().schedule(new Task(this, new DisableAudio(), "DisableAdudio"), 5000);
            //#ifdef EHS5DebugOldInd
//#                         _INDICATION.setLed(call_led, Indicator.OFF);
            //#elifdef EHS5AutostartOldInd
//#                         _INDICATION.setLed(call_led, Indicator.OFF);
            //#elifdef BGS5DebugOldInd
//#                         _INDICATION.setLed(call_led, Indicator.OFF);
            //#elifdef BGS5AutostartOldInd
//#                         _INDICATION.setLed(call_led, Indicator.OFF);
            //#elifdef BGS5Autostart
//#             _INDICATION.setLed(call_led, Indicator.ORANGE);
            //#elifdef BGS5Debug
//#                         _INDICATION.setLed(call_led, Indicator.ORANGE);
            //#elifdef EHS5Autostart
//#                          _INDICATION.setLed(call_led, Indicator.ORANGE);
            //#elifdef EHS5Debug
//#                           _INDICATION.setLed(call_led, Indicator.ORANGE);
            //#elifdef TC65Gpio
//#                         _INDICATION.blinkOff(call_led);
//#                         _INDICATION.setLed(call_led, Indicator.GREEN);
            //#elifdef TC65GpioDebug
//#                         _INDICATION.blinkOff(call_led);
//#                         _INDICATION.setLed(call_led, Indicator.GREEN);
            //#elifdef TC65SpiConcept
//#                         _INDICATION.blinkOff(call_led);
//#                         _INDICATION.setLed(call_led, Indicator.GREEN);                        
            //#elifdef TC65SpiConceptDebug
//#                         _INDICATION.blinkOff(call_led);
//#                         _INDICATION.setLed(call_led, Indicator.GREEN);
            //#elifdef TC65Spi
//#                         _INDICATION.setLed(call_led, Indicator.ORANGE);
            //#elifdef TC65SpiDebug
//#                         _INDICATION.setLed(call_led, Indicator.ORANGE);                        
            //#endif

            _CALL_STATE = 0;
            if (this._LAST_CALL != null) {
                if (_LAST_CALL.DIRECTION == 0) {
                    AkMessage2 m = new AkMessage2();
                    m.set_AkNumber(_ObjectID);
                    m.set_MesNumber(get_MessageNumber());
                    m.set_EventClass(9);
                    m.set_EventCod(9);
                    m.set_PartitionNumber(_LAST_CALL.EXTENDED_INDEX);
                    m.set_ZoneNumber(CommunicationManager.BUSY);
                    synchronized (_MESSAGES) {
                        _MESSAGES.addElement(m);
                        _MESSAGES.notifyAll();
                    }
                }
                _LAST_CALL = null;
            }
        } else if (buf.toString().indexOf("NO ANSWER") >= 0) {
            new Timer().schedule(new Task(this, new DisableAudio(), "DisableAdudio"), 5000);
            //#ifdef EHS5DebugOldInd
//#                         _INDICATION.setLed(call_led, Indicator.OFF);
            //#elifdef EHS5AutostartOldInd
//#                         _INDICATION.setLed(call_led, Indicator.OFF);
            //#elifdef BGS5DebugOldInd
//#                         _INDICATION.setLed(call_led, Indicator.OFF);
            //#elifdef BGS5AutostartOldInd
//#                         _INDICATION.setLed(call_led, Indicator.OFF);
            //#elifdef BGS5Autostart
//#             _INDICATION.setLed(call_led, Indicator.ORANGE);
            //#elifdef BGS5Debug
//#                         _INDICATION.setLed(call_led, Indicator.ORANGE);
            //#elifdef EHS5Autostart
//#                          _INDICATION.setLed(call_led, Indicator.ORANGE);
            //#elifdef EHS5Debug
//#                           _INDICATION.setLed(call_led, Indicator.ORANGE);
            //#elifdef TC65Gpio
//#                         _INDICATION.blinkOff(call_led);
//#                         _INDICATION.setLed(call_led, Indicator.GREEN);
            //#elifdef TC65GpioDebug
//#                         _INDICATION.blinkOff(call_led);
//#                         _INDICATION.setLed(call_led, Indicator.GREEN);
            //#elifdef TC65SpiConcept
//#                         _INDICATION.blinkOff(call_led);
//#                         _INDICATION.setLed(call_led, Indicator.GREEN);
            //#elifdef TC65SpiConceptDebug
//#                         _INDICATION.blinkOff(call_led);
//#                         _INDICATION.setLed(call_led, Indicator.GREEN);
            //#elifdef TC65Spi
//#                         _INDICATION.blinkOff(call_led);
//#                         _INDICATION.setLed(call_led, Indicator.ORANGE);
            //#elifdef TC65SpiDebug
//#                         _INDICATION.blinkOff(call_led);
//#                         _INDICATION.setLed(call_led, Indicator.ORANGE);                          
            //#endif
            _CALL_STATE = 0;
            if (this._LAST_CALL != null) {
                if (_LAST_CALL.DIRECTION == 0) {
                    AkMessage2 m = new AkMessage2();
                    m.set_AkNumber(_ObjectID);
                    m.set_MesNumber(get_MessageNumber());
                    m.set_EventClass(9);
                    m.set_EventCod(9);
                    m.set_PartitionNumber(_LAST_CALL.EXTENDED_INDEX);
                    m.set_ZoneNumber(CommunicationManager.NO_ANSWER);
                    synchronized (_MESSAGES) {
                        _MESSAGES.addElement(m);
                        _MESSAGES.notifyAll();
                    }
                }
                _LAST_CALL = null;
            }
        } else if (buf.toString().indexOf("+CIEV:") >= 0) {
            ciev_handler(buf.toString());
        } else if (buf.toString().indexOf("+CRING: VOICE") >= 0) {
            System.out.println("_Call_State=" + _CALL_STATE);
            if (_CALL_STATE != 2) {
                _ATC.send("AT+CLCC\r", this);

//                            if (cl != null) {
//                                if (this._LAST_CALL == null) {
//                                    if (check_call(cl)) {
//                                        set_Call(cl);
//                                        answer_on_call(cl);
//
//                                    } else {
//                                        hungup_call(cl, (char) 0x21);
//                                    }
//                                }
//                            }
                millis = 100;
            }
        } else if (buf.toString().indexOf("+CMTI:") >= 0) {
            _FAR_WAYS.ExternalATEvent(buf.toString());
        } else if (buf.toString().indexOf("+USER:") >= 0) {

        } else if (buf.toString().indexOf("+SERVER:") >= 0) {

        } else if (buf.toString().indexOf("+CREG:") >= 0) {
            if (buf.toString().indexOf("+CREG: 0") >= 0) {
                _REGISTRATION = 0;
                if (_MODE != CommunicationManager.INTERNAL_BLOCKED_MODE) {
                    _OLD_MODE = _MODE;
                }
                _MODE = CommunicationManager.INTERNAL_BLOCKED_MODE;

                _FAR_WAYS.set_Mode(_MODE);
//                            check_AirPlane_Mode();
//                            synchronized (_MUTEX) {
//                                try {
//                                    _MUTEX.wait(3000);
//                                } catch (InterruptedException ex) {
//                                    ex.printStackTrace();
//                                }
//                                _MUTEX.notifyAll();
//                            }
//                            if (_STATE != _RESTART_GSM_STATE) {
//                                _PREV_STATE = _STATE;
//                                _STATE = _RESTART_GSM_STATE;
//                            }                            
            } else if (buf.toString().indexOf("+CREG: 3") >= 0) {
                _REGISTRATION = 3;
                if (_MODE != CommunicationManager.INTERNAL_BLOCKED_MODE) {
                    _OLD_MODE = _MODE;
                }
                _MODE = CommunicationManager.INTERNAL_BLOCKED_MODE;

                _FAR_WAYS.set_Mode(_MODE);
                _FAR_WAYS.pause();
//                            check_AirPlane_Mode();
//                            synchronized (_MUTEX) {
//                                try {
//                                    _MUTEX.wait(3000);
//                                } catch (InterruptedException ex) {
//                                    ex.printStackTrace();
//                                }
//                                _MUTEX.notifyAll();
//                            }
//                            if (_STATE != _RESTART_GSM_STATE) {
//                                _PREV_STATE = _STATE;
//                                _STATE = _RESTART_GSM_STATE;
//                            }
            } else if (buf.toString().indexOf("+CREG: 2") > -1) {
                _REGISTRATION = 2;
                if (_MODE != CommunicationManager.INTERNAL_BLOCKED_MODE) {
                    _OLD_MODE = _MODE;
                }
                _MODE = CommunicationManager.INTERNAL_BLOCKED_MODE;

                _FAR_WAYS.set_Mode(_MODE);
                _FAR_WAYS.pause();
            } else if (buf.toString().indexOf("+CREG: 1") > -1 || buf.toString().indexOf("+CREG: 5") > -1) {
                if (buf.toString().indexOf(": 1") > -1) {
                    _REGISTRATION = 1;
                } else {
                    _REGISTRATION = 5;
                }
                _FAR_WAYS.resume();
                if (_MODE != CommunicationManager.EXTERNAL_BLOCKED_MODE) {
                    _MODE = _OLD_MODE;
                }
                _FAR_WAYS.set_Mode(_MODE);
                if(_STATE != _WORK_STATE && _STATE!=_INIT_AT_STATE){
                    _STATE = _WORK_STATE;
                }
                String[] tokens = Parser.Parse(buf.toString(), ':');
                if (tokens.length == 2) {
                    String[] slices = Parser.Parse(tokens[1], ',');
                    if (slices.length == 4) {
                        int x = 0;
                        try {
                            x = Integer.parseInt(slices[3]);
                        } catch (NumberFormatException ex) {
                            x = 0;
                        }
                        switch (x) {
                            case 0:
                                _NET_MODE = CommunicationManager._NET_MODE_2G;
                                break;
                            case 1:
                                _NET_MODE = CommunicationManager._NET_MODE_3G;
                                break;
                            case 2:
                                _NET_MODE = CommunicationManager._NET_MODE_3G;
                                break;
                            case 3:
                                _NET_MODE = CommunicationManager._NET_MODE_2G;
                                break;
                            case 4:
                                _NET_MODE = CommunicationManager._NET_MODE_3G;
                                break;
                            case 5:
                                _NET_MODE = CommunicationManager._NET_MODE_3G;
                                break;
                            case 6:
                                _NET_MODE = CommunicationManager._NET_MODE_3G;
                                break;
                        }
                    } else {
                        _RACCT = CommunicationManager._NET_MODE_2G;
                    }
                }
            }

        }
        return millis;
    }

    private void checkThreads() {
        try {
            if (_COM != null) {
                if (!_COM.isALive()) {
                    _COM.restart();
                }
            }
            if (_PacketHandler != null) {
                if (!_PacketHandler.isALive()) {
                    _PacketHandler.restart();
                }
            }
            if (_INDICATION != null) {
                if (!_INDICATION.isALive()) {
                    _INDICATION.restart();
                }
            }
            if (_FAR_WAYS != null) {
                if (!_FAR_WAYS.isALive()) {
                    _FAR_WAYS.restart();
                }
            }
            if (_ATC != null) {
                if (!_ATC.isALive()) {
                    _ATC.restart();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     *
     * @param string
     */
    public void ATEvent(String string) {
//^SYSLOADING
//^SYSSTART
//+PBREADY

        try {
            System.out.println("[Terminal] ATEvent: " + string.trim());
            if (string.indexOf("^SYSSTART AIRPLANE MODE") > -1) {

                if (!_AIRPLANE_MODE && _STATE!=_RESTART_GSM_STATE) {
                    _SYSSTART = true;
                    _AIRPLANE_MODE = true;
                   //_PREV_STATE = _STATE;
                    _STATE = _RESTART_GSM_STATE;
                }
                return;
            } 
            if (string.indexOf("+CFUN: 4") > -1) {
                System.out.println("Oh oh oh!!!! Not handled cfun 4!!!!");
                if (!_AIRPLANE_MODE && _STATE != _RESTART_GSM_STATE) {
                    _SYSSTART = true;
                    _AIRPLANE_MODE = true;
                    //_PREV_STATE = _STATE;
                    _STATE = _RESTART_GSM_STATE;
                }
                return;    
            } 


            if (string.indexOf("PBREADY") > -1) {
                System.out.println("Set PBREADY to true;");
                _PBREADY = true;
                if(_START_TIMER!=null){
                    _START_TIMER.cancel();
                }
            }

            if (string.indexOf("SYSSTART") > -1) {
                System.out.println("Set SYSSTART to true;");
                _SYSSTART = true;
                if(_START_TIMER!=null){
                    _START_TIMER.cancel();
                }                                
            }
                
            if (string.indexOf("+CIEV: service,0") > -1) {
                _SERVICE = 0;
                //#ifdef EHS5AutostartOldInd
//#             _INDICATION.setLed(work_led, Indicator.GREEN);
//#             _INDICATION.set_BlinkSpeed(_INDICATION.HI_SPEED);
                //#elifdef EHS5DebugOldInd
//#             _INDICATION.setLed(work_led, Indicator.GREEN);
//#             _INDICATION.set_BlinkSpeed(Indicator.HI_SPEED);
                //#elifdef BGS5AutostartOldInd
//#             _INDICATION.setLed(work_led, Indicator.GREEN);
//#             _INDICATION.set_BlinkSpeed(_INDICATION.HI_SPEED);
                //#elifdef BGS5DebugOldInd
//#             _INDICATION.setLed(work_led, Indicator.GREEN);
//#             _INDICATION.set_BlinkSpeed(Indicator.HI_SPEED);
                //#elifdef TC65SpiConcept
//#             _INDICATION.setLed(work_led, Indicator.GREEN);
//#             _INDICATION.set_BlinkSpeed(_INDICATION.HI_SPEED);
                //#elifdef TC65SpiConceptDebug
//#             _INDICATION.setLed(work_led, Indicator.GREEN);
//#             _INDICATION.set_BlinkSpeed(Indicator.HI_SPEED);              
                //#elifdef TC65Gpio
//#             _INDICATION.set_BlinkSpeed(Indicator.HI_SPEED);
//#             _INDICATION.setLed(work_led, Indicator.GREEN);
                //#elifdef TC65GpioDebug
//#             _INDICATION.set_BlinkSpeed(Indicator.HI_SPEED);
//#             _INDICATION.setLed(work_led, Indicator.GREEN);            
                //#else
                _INDICATION.setLed(work_led, Indicator.RED);
                //#endif                  
                if (_AIRPLANE_MODE) {
                    return;
                } else if (_STATE == _RESTART_GSM_STATE) {
                    return;
                } else {
//                    _PREV_STATE = _STATE;
                    _STATE = _RESTART_GSM_STATE;
                    _FAR_WAYS.set_Mode(CommunicationManager.INTERNAL_BLOCKED_MODE);
                    _FAR_WAYS.pause();
                    System.out.println("Set STATE to RESTART_GSM_STATE");
                    return;
                }
            } 
            if (string.indexOf("+CIEV: service,1") > -1) {
                _SERVICE = 1;
            _FAR_WAYS.resume();
                if (_MODE != CommunicationManager.EXTERNAL_BLOCKED_MODE) {
                    _MODE = _OLD_MODE;
                    _FAR_WAYS.set_Mode(_MODE);
                    //#ifdef EHS5AutostartOldInd
//#                 _INDICATION.setLed(work_led, Indicator.GREEN);
//#                 _INDICATION.set_BlinkSpeed(_INDICATION.SLO_SPEED);
                    //#elifdef EHS5DebugOldInd
//#                 _INDICATION.setLed(work_led, Indicator.GREEN);
//#                     _INDICATION.set_BlinkSpeed(_INDICATION.SLO_SPEED);
                    //#elifdef BGS5AutostartOldInd
//#                 _INDICATION.setLed(work_led, Indicator.GREEN);
//#                 _INDICATION.set_BlinkSpeed(_INDICATION.SLO_SPEED);
                    //#elifdef BGS5DebugOldInd
//#                 _INDICATION.setLed(work_led, Indicator.GREEN);
//#                 _INDICATION.set_BlinkSpeed(_INDICATION.SLO_SPEED);
                    //#elifdef TC65SpiConcept
//#                 _INDICATION.setLed(work_led, Indicator.GREEN);
//#                    _INDICATION.set_BlinkSpeed(_INDICATION.SLO_SPEED);
                    //#elifdef TC65SpiConceptDebug
//#                 _INDICATION.setLed(work_led, Indicator.GREEN);
//#                     _INDICATION.set_BlinkSpeed(_INDICATION.SLO_SPEED);                
                    //#elifdef TC65Gpio
//#                 _INDICATION.set_BlinkSpeed(Indicator.SLO_SPEED);
//#                 _INDICATION.setLed(work_led, Indicator.GREEN);
                    //#elifdef TC65GpioDebug
//#                 _INDICATION.set_BlinkSpeed(Indicator.SLO_SPEED);
//#                 _INDICATION.setLed(work_led, Indicator.GREEN);                
                    //#else
                    _INDICATION.setLed(work_led, Indicator.GREEN);
                    //#endif                        
//                        _INDICATION.setLed(work_led, Indicator.GREEN);
                }
                return;
            }
            if (string.indexOf("+CREG:") > -1) {
                if (string.indexOf("+CREG: 0") > -1) {
                    _REGISTRATION = 0;
                    synchronized (_TERM_STATE) {
                        _TERM_STATE.REG = TerminalState.REG_NOTREG;
                        _TERM_STATE.notifyAll();
                    }
                    if (_AIRPLANE_MODE) {
                        return;
                    } else if (_STATE == _RESTART_GSM_STATE) {
                        return;
                    } else {
                        //_PREV_STATE = _STATE;
                        _STATE = _RESTART_GSM_STATE;
                        _FAR_WAYS.set_Mode(CommunicationManager.INTERNAL_BLOCKED_MODE);
                        _FAR_WAYS.pause();
                        System.out.println("Set STATE to RESTART_GSM_STATE");
                        return;
                    }
                } else if (string.indexOf("+CREG: 3") > -1) {
                    _REGISTRATION = 3;
                    synchronized (_TERM_STATE) {
                        _TERM_STATE.REG = TerminalState.REG_DENIED;
                        _TERM_STATE.notifyAll();
                    }
                    if (_AIRPLANE_MODE) {
                        return;
                    } else if (_STATE == _RESTART_GSM_STATE) {
                        return;

                    } else {
                        //_PREV_STATE = _STATE;
                        _STATE = _RESTART_GSM_STATE;
                        _FAR_WAYS.set_Mode(CommunicationManager.INTERNAL_BLOCKED_MODE);
                        _FAR_WAYS.pause();
                        System.out.println("Set STATE to RESTART_GSM_STATE");
                        return;
                    }
                } else if (string.indexOf("+CREG: 2") > -1) {
                    synchronized (_TERM_STATE) {
                        _TERM_STATE.REG = TerminalState.REG_SEARCHING;
                        _TERM_STATE.notifyAll();
                    }
                    _REGISTRATION = 2;
                    _MODE = CommunicationManager.INTERNAL_BLOCKED_MODE;
                    _FAR_WAYS.set_Mode(CommunicationManager.INTERNAL_BLOCKED_MODE);
                    _FAR_WAYS.pause();
                    _ATC.send("AT+COPS=0\r", this);
                    return;
                } else if (string.indexOf("+CREG: 1") > -1) {
                    synchronized (_TERM_STATE) {
                        _TERM_STATE.REG = TerminalState.REG_TO_HOME;
                        _TERM_STATE.notifyAll();
                    }                   
                    _REGISTRATION = 1;                    
                    if (_PREV_STATE == _RESTART_GSM_STATE) {
                        _STATE = _WORK_STATE;
                        _FAR_WAYS.set_Mode(CommunicationManager.N0RMAL_MODE);
                        _FAR_WAYS.resume();
                        return;
                    }
                } else if (string.indexOf("+CREG: 5") > -1) {
                    synchronized (_TERM_STATE) {
                        _TERM_STATE.REG = TerminalState.REG_ROAMING;
                        _TERM_STATE.notifyAll();
                    }                    
                    _REGISTRATION = 5;
                    if (_PREV_STATE == _RESTART_GSM_STATE) {
                        _STATE = _WORK_STATE;
                        _FAR_WAYS.set_Mode(CommunicationManager.N0RMAL_MODE);
                        _FAR_WAYS.resume();
                        return;
                    }
                } else if (string.indexOf("+CREG: 4") > -1) {
                    synchronized (_TERM_STATE) {
                        _TERM_STATE.REG = TerminalState.REG_UNKNOWN;
                        _TERM_STATE.notifyAll();
                    }                    
                    _REGISTRATION = 4;
                    return;
                }
            }

            int counter = 0;
            synchronized (_QUEUE) {
//                if (string.indexOf("service,0") >= 0) {
//                    if (!_QUEUE.contains(string)) {
//                        _QUEUE.addElement(string.trim());
//                    }
//                } else {
//                    _QUEUE.addElement(string.trim());
//                }
                _QUEUE.addElement(string.trim());
                counter = _QUEUE.size();
                _QUEUE.notifyAll();
            }
            System.out.println("[Terminal] Number of events in QUEUE: " + counter);
        } catch (IllegalMonitorStateException ex) {
        } catch (Exception ex) {
        } catch (Error err) {
        }
    }

    /**
     *
     * @param bln
     */
    public void RINGChanged(boolean bln) {

    }

    /**
     *
     * @param bln
     */
    public void DCDChanged(boolean bln) {

    }

    /**
     *
     * @param bln
     */
    public void DSRChanged(boolean bln) {

    }

    /**
     *
     * @param bln
     */
    public void CONNChanged(boolean bln) {

    }

    /**
     *
     * @param bt
     */
    public void buttonPressed(int bt) {

        System.out.println("Bt. " + bt);
        if (_MODE != CommunicationManager.EXTERNAL_BLOCKED_MODE) {

            //#ifdef TC65Gpio
//#             try {
//#                 Manager.playTone(0x10, 100, 100);
//#             } catch (MediaException me) {
//#             } catch (Exception ex) {
//#             } catch (Error err) {
//#             }
            //#elifdef TC65GpioDebug
//#             try {
//#                 Manager.playTone(0x10, 100, 100);
//#             } catch (MediaException me) {
//#             } catch (Exception ex) {
//#             } catch (Error err) {
//#             }            
            //#elifdef TC65SpiConcept
//#             try {
//#                 Manager.playTone(0x10, 100, 100);
//#             } catch (MediaException me) {
//#                 me.printStackTrace();
//#             } catch (Exception ex) {
//#                 ex.printStackTrace();
//#             } catch (Error err) {
//#                 err.printStackTrace();
//#             }
            //#elifdef TC65SpiConceptDebug
//#             try {
//#                 Manager.playTone(0x10, 100, 100);
//#             } catch (MediaException me) {
//#                 me.printStackTrace();
//#             } catch (Exception ex) {
//#                 ex.printStackTrace();
//#             } catch (Error err) {
//#                 err.printStackTrace();
//#             }              
            //#elifdef TC65Spi
//#             try {
//#                 Manager.playTone(0x10, 100, 100);
//#             } catch (MediaException me) {
//#                 me.printStackTrace();
//#             } catch (Exception ex) {
//#                 ex.printStackTrace();
//#             } catch (Error err) {
//#                 err.printStackTrace();
//#             }
            //#endif
            button_std(bt);
        }
    }

    private void button_std(int bt) {
        StringBuffer bf = new StringBuffer();

        try {
            if (bt > 3 && bt < 10) {
                AkMessage2 msg = new AkMessage2();
                msg.set_AkNumber(_ObjectID);
                msg.set_MesNumber(get_MessageNumber());
                msg.set_EventClass(13);
                msg.set_EventCod(1);
                msg.set_PartitionNumber(0);
                msg.set_ZoneNumber((bt - 3));
                synchronized (_MESSAGES) {
                    _MESSAGES.addElement(msg);
                    _MESSAGES.notifyAll();
                }
                this._INDICATION.setLed((byte) ((bt - 3) + 1), Indicator.ORANGE);
            } else {
                switch (bt) {
                    case 12:
                        if (_CALL_STATE == 1 || _CALL_STATE == 2) {
                            break;
                        }
//                        synchronized(_ATC)
                         {
                            //#ifdef TC65Spi 
//#                              _ATC.send("AT^SM20=0\r");
                            //#elifdef TC65SpiDebug
//#                             _ATC.send("AT^SM20=0\r");                             
                            //#elifdef TC65SpiConcept 
//#                              _ATC.send("AT^SM20=0\r");
                            //#elifdef TC65SpiConceptDebug
//#                              _ATC.send("AT^SM20=0\r");                              
                            //#elifdef TC65Gpio
//#                             _ATC.send("AT^SM20=0\r");   
//#                             _INDICATION.blinkOff(call_led);
//#                             _INDICATION.setLed(call_led, Indicator.GREEN);
                            //#elifdef TC65GpioDebug
//#                             _ATC.send("AT^SM20=0\r");
                            //#endif                             

                            enable_Audio();
                            _ATC.send("AT+CPBS=\"SM\"\r");
                            _ATC.send("AT+CPBR=1\r");
                            //#ifdef TC65Spi 
//#                                 _ATC.send("ATD>SM1;\r",this);
//#                                 _ATC.send("AT^SM20=1\r");
                            //#elifdef TC65SpiDebug
//#                             _ATC.send("ATD>SM1;\r",this);                            
//#                             _ATC.send("AT^SM20=1\r");                             
                            //#elifdef TC65SpiConcept 
//#                                 _ATC.send("ATD>SM1;\r",this);                             
//#                                 _ATC.send("AT^SM20=1\r");
                            //#elifdef TC65SpiConceptDebug
//#                             _ATC.send("ATD>SM1;\r",this);                              
//#                             _ATC.send("AT^SM20=1\r");                              
                            //#elifdef TC65Gpio
//#                                 _ATC.send("ATD>SM1;\r",this);                             
//#                                 _ATC.send("AT^SM20=1\r");                           
                            //#elifdef TC65GpioDebug
//#                                 _ATC.send("ATD>SM1;\r",this);                              
//#                                 _ATC.send("AT^SM20=1\r");
                            //#else
                            _ATC.send("ATD>SM1;\r", this);
                            //#endif
//                        _ATC.notifyAll();
                        }
                        _ATC.send("AT+CLCC\r", this);
//                        if (bf.toString().indexOf("OK") >= 0) {
//                            _CALL_STATE = 1;
//                            this._LAST_CALL = get_Call();
//                            if (_LAST_CALL != null) {
//                                this._LAST_CALL.EXTENDED_INDEX = 1;
//                            }
//                        }
                        break;
                    case 13:
                        //#ifdef EHS5AutostartOldInd
//#                         System.out.println("BT13 disabled");
                        //#elifdef EHS5DebugOldInd
//#                        System.out.println("BT13 disabled");
                        //#elifdef BGS5AutostartOldInd
//#                         System.out.println("BT13 disabled");
                        //#elifdef BGS5DebugOldInd
//#                         System.out.println("BT13 disabled");
                        //#elifdef TC65SpiConcept
//#                         System.out.println("BT13 disabled");
                        //#elifdef TC65SpiConceptDebug
//#                        System.out.println("BT13 disabled");                         
                        //#else
                        if (_CALL_STATE == 1 || _CALL_STATE == 2) {
                            break;
                        }
//                        synchronized(_ATC)
                         {
                            //#ifdef TC65Spi 
//#                              _ATC.send("AT^SM20=0\r");
                            //#elifdef TC65SpiDebug
//#                             _ATC.send("AT^SM20=0\r");                                                          
                            //#elifdef TC65Gpio
//#                             _ATC.send("AT^SM20=0\r");                           
                            //#elifdef TC65GpioDebug
//#                             _ATC.send("AT^SM20=0\r");
                            //#endif 

                            enable_Audio();
                            _ATC.send("AT+CPBS=\"SM\"\r");
                            _ATC.send("AT+CPBR=2\r");
                            _ATC.send("ATD>SM2;\r", this);
                            //#ifdef TC65Spi 
//#                              _ATC.send("AT^SM20=1\r");
                            //#elifdef TC65SpiDebug
//#                             _ATC.send("AT^SM20=1\r");                             
                            //#elifdef TC65Gpio
//#                             _ATC.send("AT^SM20=1\r");                           
                            //#elifdef TC65GpioDebug
//#                             _ATC.send("AT^SM20=1\r");
                            //#endif 
                            //_ATC.notifyAll();
                        }
                        _ATC.send("AT+CLCC\r", this);
//                        if (bf.toString().indexOf("OK") >= 0) {
//                            _CALL_STATE = 1;
//                            this._LAST_CALL = get_Call();
//                            if (_LAST_CALL != null) {
//                                this._LAST_CALL.EXTENDED_INDEX = 2;
//                            }
//                        }
                        //#endif
                        break;
                    case 14:
                        //#ifdef EHS5AutostartOldInd
//#                         System.out.println("BT13 disabled");
                        //#elifdef EHS5DebugOldInd
//#                        System.out.println("BT13 disabled");
                        //#elifdef BGS5AutostartOldInd
//#                         System.out.println("BT13 disabled");
                        //#elifdef BGS5DebugOldInd
//#                         System.out.println("BT13 disabled");
                        //#elifdef TC65SpiConcept
//#                         System.out.println("BT13 disabled");
                        //#elifdef TC65SpiConceptDebug
//#                        System.out.println("BT13 disabled"); 
                        //#else
                        if (_CALL_STATE == 1 || _CALL_STATE == 2) {
                            break;
                        }
//                        synchronized(_ATC)
                         {
                            //#ifdef TC65Spi 
//#                              _ATC.send("AT^SM20=0\r");
                            //#elifdef TC65SpiDebug
//#                             _ATC.send("AT^SM20=0\r");                                                          
                            //#elifdef TC65Gpio
//#                             _ATC.send("AT^SM20=0\r");                           
                            //#elifdef TC65GpioDebug
//#                             _ATC.send("AT^SM20=0\r");
                            //#endif 

                            enable_Audio();
                            _ATC.send("AT+CPBS=\"SM\"\r");
                            _ATC.send("AT+CPBR=3\r");
                            _ATC.send("ATD>SM3;\r", this);
                            //#ifdef TC65Spi 
//#                              _ATC.send("AT^SM20=1\r");
                            //#elifdef TC65SpiDebug
//#                             _ATC.send("AT^SM20=1\r");                                                          
                            //#elifdef TC65Gpio
//#                             _ATC.send("AT^SM20=1\r");                           
                            //#elifdef TC65GpioDebug
//#                             _ATC.send("AT^SM20=1\r");
                            //#endif 
                            //_ATC.notifyAll();
                        }
                        _ATC.send("AT+CLCC\r", this);
//                        if (bf.toString().indexOf("OK") >= 0) {
//                            _CALL_STATE = 1;
//                            this._LAST_CALL = get_Call();
//                            if (_LAST_CALL != null) {
//                                this._LAST_CALL.EXTENDED_INDEX = 3;
//                            }
//                        }
                        //#endif
                        break;

                    case 15: //                        synchronized (_ATC) 
                    {
                        //#ifdef TC65Spi 
//#                              _ATC.send("AT^SM20=0\r");
                        //#elifdef TC65SpiDebug
//#                             _ATC.send("AT^SM20=0\r");                                                          
                        //#elifdef TC65Gpio
//#                             _ATC.send("AT^SM20=0\r");                           
                        //#elifdef TC65GpioDebug
//#                             _ATC.send("AT^SM20=0\r");
                        //#endif 

                        _ATC.send("ATH\r", this);
                        //#ifdef TC65Spi 
//#                              _ATC.send("AT^SM20=1\r");
                        //#elifdef TC65SpiDebug
//#                             _ATC.send("AT^SM20=1\r");                                                          
                        //#elifdef TC65Gpio
//#                             _ATC.send("AT^SM20=1\r");                           
                        //#elifdef TC65GpioDebug
//#                             _ATC.send("AT^SM20=1\r");
                        //#endif                         
                        disable_Audio();
//                            _ATC.notifyAll();

                    }
                    System.out.println("Try set leds to green");
                    if(_LAST_CALL==null){
                            //#ifdef TC65Spi 
//#                             _INDICATION.setLed((byte)2, Indicator.GREEN);
//#                             _INDICATION.setLed((byte)3, Indicator.GREEN);
//#                             _INDICATION.setLed((byte)4, Indicator.GREEN);
//#                             _INDICATION.setLed((byte)5, Indicator.GREEN);
//#                             _INDICATION.setLed((byte)6, Indicator.GREEN);
//#                             _INDICATION.setLed((byte)7, Indicator.GREEN);
                            //#elifdef TC65SpiDebug
//#                             _INDICATION.setLed((byte)2, Indicator.GREEN);
//#                             _INDICATION.setLed((byte)3, Indicator.GREEN);
//#                             _INDICATION.setLed((byte)4, Indicator.GREEN);
//#                             _INDICATION.setLed((byte)5, Indicator.GREEN);
//#                             _INDICATION.setLed((byte)6, Indicator.GREEN);
//#                             _INDICATION.setLed((byte)7, Indicator.GREEN);                                                        
                            //#elifdef BGS5Autostart
//#                             System.out.println("Set leds to green!!!!!");
//#                             _INDICATION.setLed((byte)2, Indicator.GREEN);
//#                             _INDICATION.setLed((byte)3, Indicator.GREEN);
//#                             _INDICATION.setLed((byte)4, Indicator.GREEN);
//#                             _INDICATION.setLed((byte)5, Indicator.GREEN);
//#                             _INDICATION.setLed((byte)6, Indicator.GREEN);
//#                             _INDICATION.setLed((byte)7, Indicator.GREEN);                            
                            //#elifdef BGS5Debug
//#                             _INDICATION.setLed((byte)2, Indicator.GREEN);
//#                             _INDICATION.setLed((byte)3, Indicator.GREEN);
//#                             _INDICATION.setLed((byte)4, Indicator.GREEN);
//#                             _INDICATION.setLed((byte)5, Indicator.GREEN);
//#                             _INDICATION.setLed((byte)6, Indicator.GREEN);
//#                             _INDICATION.setLed((byte)7, Indicator.GREEN);                            
                            //#elifdef EHS5Autostart
//#                             _INDICATION.setLed((byte)2, Indicator.GREEN);
//#                             _INDICATION.setLed((byte)3, Indicator.GREEN);
//#                             _INDICATION.setLed((byte)4, Indicator.GREEN);
//#                             _INDICATION.setLed((byte)5, Indicator.GREEN);
//#                             _INDICATION.setLed((byte)6, Indicator.GREEN);
//#                             _INDICATION.setLed((byte)7, Indicator.GREEN);                            
                            //#elifdef EHS5Debug
//#                             _INDICATION.setLed((byte)2, Indicator.GREEN);
//#                             _INDICATION.setLed((byte)3, Indicator.GREEN);
//#                             _INDICATION.setLed((byte)4, Indicator.GREEN);
//#                             _INDICATION.setLed((byte)5, Indicator.GREEN);
//#                             _INDICATION.setLed((byte)6, Indicator.GREEN);
//#                             _INDICATION.setLed((byte)7, Indicator.GREEN);                            
                            //#endif
                    }else{
                        System.out.println("_LAST_CALL !=null");
                    }
//                    if (bf.toString().indexOf("OK") >= 0) {
//                        _CALL_STATE = 0;
//                        _INDICATION.setLed(call_led, Indicator.OFF);
//                        System.out.println("Terminate call");
//                    }
                    break;
                    default:
                        System.out.println("Terminal: Nothing to do.");
                        break;

                }
            }
        } catch (IllegalStateException ex) {
        } catch (IllegalArgumentException ex) {
        }
    }

    /**
     *
     * @param input
     * @param state
     */
    public void inputStateChanged(byte input, byte state) {
        System.out.println("InputStateChanged:" + input + "," + state);
    }

    private void ciev_handler(String msg) {
        if (msg.indexOf("ciphcall") >= 0) {
            return;
        } else if (msg.indexOf("call,1") >= 0) {
            _INDICATION.setLed(call_led, Indicator.GREEN);
            enable_Audio();
            if (_CALL_TIME > 0) {
                try {

                    if (_CALL_TIMER == null) {
                        _CALL_TIMER = new Timer();
                    }
                    Task endCall = new Task(this, new EndCallEvent(), "EndCall");
                    _CALL_TIMER.schedule(endCall, _CALL_TIME * 1000);
                } catch (Exception ex) {
                }
            }
            _CALL_STATE = 2;
            _ATC.send("AT+CLCC\r", this);
//            Call cl = get_Call();
//            if (cl == null) {
//                return;
//            }
//
//            if (_LAST_CALL != null) {
//                if (cl.NUMBER.compareTo(_LAST_CALL.NUMBER) == 0 && cl.DIRECTION == _LAST_CALL.DIRECTION) {
//                    cl.EXTENDED_INDEX = _LAST_CALL.EXTENDED_INDEX;
//                }
//                _LAST_CALL = cl;
//            } else if (cl.DIRECTION == 1) {
//                set_Call(cl);
//            }

        } else if (msg.indexOf("call,0") >= 0) {
            _INDICATION.setLed(call_led, Indicator.OFF);
            disable_Audio();//NOREPLACE
            _CALL_STATE = 0;
            if (this._LAST_CALL != null) {
                if (_LAST_CALL.DIRECTION == 0) {
                    AkMessage2 m = new AkMessage2();
                    m.set_AkNumber(_ObjectID);
                    m.set_MesNumber(get_MessageNumber());
                    m.set_EventClass(9);
                    m.set_EventCod(9);
                    m.set_PartitionNumber(_LAST_CALL.EXTENDED_INDEX);
                    if (_LAST_CALL.STATE == 0) {
                        m.set_ZoneNumber(0);
                    } else {
                        m.set_ZoneNumber(CommunicationManager.ERROR);
                    }
                    synchronized (_MESSAGES) {
                        _MESSAGES.addElement(m);
                        _MESSAGES.notifyAll();
                    }
                } else if (_LAST_CALL.DIRECTION == 1) {
                    AkMessage2 m = new AkMessage2();
                    m.set_AkNumber(_ObjectID);
                    m.set_MesNumber(get_MessageNumber());
                    m.set_EventClass(13);
                    m.set_EventCod(2);
                    m.set_PartitionNumber(_LAST_CALL.EXTENDED_INDEX);
                    if (_LAST_CALL.STATE == 0) {
                        m.set_ZoneNumber(0);
                    } else {
                        m.set_ZoneNumber(CommunicationManager.ERROR);
                    }
                    synchronized (_MESSAGES) {
                        _MESSAGES.addElement(m);
                        _MESSAGES.notifyAll();
                    }
                }
                if (_CALL_TIMER != null) {
                    try {
                        _CALL_TIMER.cancel();
                        _CALL_TIMER = null;
                    } catch (Exception ex) {
                    }
                }

            }
            this._LAST_CALL = null;
            try {
                _ATC.send("AT^SRTC=1,0\r", this);
            } catch (IllegalStateException ex) {
            } catch (IllegalArgumentException ex) {
            }
        } else if (msg.indexOf("service,1") >= 0) {
            _FAR_WAYS.resume();
            if (_MODE != CommunicationManager.EXTERNAL_BLOCKED_MODE) {
                _MODE = _OLD_MODE;
                _FAR_WAYS.set_Mode(_MODE);
                //#ifdef EHS5AutostartOldInd
//#                 _INDICATION.setLed(work_led, Indicator.GREEN);
//#                 _INDICATION.set_BlinkSpeed(_INDICATION.SLO_SPEED);
                //#elifdef EHS5DebugOldInd
//#                 _INDICATION.setLed(work_led, Indicator.GREEN);
//#                     _INDICATION.set_BlinkSpeed(_INDICATION.SLO_SPEED);
                //#elifdef BGS5AutostartOldInd
//#                 _INDICATION.setLed(work_led, Indicator.GREEN);
//#                 _INDICATION.set_BlinkSpeed(_INDICATION.SLO_SPEED);
                //#elifdef BGS5DebugOldInd
//#                 _INDICATION.setLed(work_led, Indicator.GREEN);
//#                 _INDICATION.set_BlinkSpeed(_INDICATION.SLO_SPEED);
                //#elifdef TC65SpiConcept
//#                 _INDICATION.setLed(work_led, Indicator.GREEN);
//#                    _INDICATION.set_BlinkSpeed(_INDICATION.SLO_SPEED);
                //#elifdef TC65SpiConceptDebug
//#                 _INDICATION.setLed(work_led, Indicator.GREEN);
//#                     _INDICATION.set_BlinkSpeed(_INDICATION.SLO_SPEED);                
                //#elifdef TC65Gpio
//#                 _INDICATION.set_BlinkSpeed(Indicator.SLO_SPEED);
//#                 _INDICATION.setLed(work_led, Indicator.GREEN);
                //#elifdef TC65GpioDebug
//#                 _INDICATION.set_BlinkSpeed(Indicator.SLO_SPEED);
//#                 _INDICATION.setLed(work_led, Indicator.GREEN);                
                //#else
                _INDICATION.setLed(work_led, Indicator.GREEN);
                //#endif                        
//                        _INDICATION.setLed(work_led, Indicator.GREEN);
            }
            if (_SERVICE != 1) {
                _SERVICE = (char) 1;

//                String name = "";
//                try {
//                    name = getCurrOpName();
//                } catch (IllegalStateException ex) {
//                } catch (Exception ex) {
//                } catch (Error ex) {
//                }
                set_JavaTCPbyName(_CURRENT_OPERATOR_NAME);

            }
            for (int i = 0; i < 50; i++) {
                if (check_Net_Mode()) {
                    break;
                }
            }
        } else if (msg.indexOf("service,0") >= 0) {
            if (_MODE != CommunicationManager.INTERNAL_BLOCKED_MODE) {
                _OLD_MODE = _MODE;
            }
            _MODE = CommunicationManager.INTERNAL_BLOCKED_MODE;

            _FAR_WAYS.set_Mode(_MODE);
            //#ifdef EHS5AutostartOldInd
//#             _INDICATION.setLed(work_led, Indicator.GREEN);
//#             _INDICATION.set_BlinkSpeed(_INDICATION.HI_SPEED);
            //#elifdef EHS5DebugOldInd
//#             _INDICATION.setLed(work_led, Indicator.GREEN);
//#             _INDICATION.set_BlinkSpeed(Indicator.HI_SPEED);
            //#elifdef BGS5AutostartOldInd
//#             _INDICATION.setLed(work_led, Indicator.GREEN);
//#             _INDICATION.set_BlinkSpeed(_INDICATION.HI_SPEED);
            //#elifdef BGS5DebugOldInd
//#             _INDICATION.setLed(work_led, Indicator.GREEN);
//#             _INDICATION.set_BlinkSpeed(Indicator.HI_SPEED);
            //#elifdef TC65SpiConcept
//#             _INDICATION.setLed(work_led, Indicator.GREEN);
//#             _INDICATION.set_BlinkSpeed(_INDICATION.HI_SPEED);
            //#elifdef TC65SpiConceptDebug
//#             _INDICATION.setLed(work_led, Indicator.GREEN);
//#             _INDICATION.set_BlinkSpeed(Indicator.HI_SPEED);              
            //#elifdef TC65Gpio
//#             _INDICATION.set_BlinkSpeed(Indicator.HI_SPEED);
//#             _INDICATION.setLed(work_led, Indicator.GREEN);
            //#elifdef TC65GpioDebug
//#             _INDICATION.set_BlinkSpeed(Indicator.HI_SPEED);
//#             _INDICATION.setLed(work_led, Indicator.GREEN);            
            //#else
            _INDICATION.setLed(work_led, Indicator.RED);
            //#endif                        
//                        _INDICATION.setLed(work_led, Indicator.RED);
            _SERVICE = (char) 0;
            check_AirPlane_Mode();
            synchronized (_MUTEX) {
                try {
                    _MUTEX.wait(3000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                _MUTEX.notifyAll();
            }
            if (_STATE != _RESTART_GSM_STATE) {
               // _PREV_STATE = _STATE;
                _STATE = _RESTART_GSM_STATE;
                return;
            }

            //!!!!!!!!!!!!тут перерегистрация в сети через режим в самолёте.
//            if (!_RESTART_GSM) 
            {
//                enable_AirPlane_Mode();
//                if (reinitGsm() < 0) {
//                    ATEvent("+CIEV: service,0");
//                }
            }
        } //        else if (msg.indexOf("service,1") >= 0) {
        //            if (_MODE != CommunicationManager.EXTERNAL_BLOCKED_MODE) {
        //                _MODE = _OLD_MODE;
        //                _FAR_WAYS.set_Mode(_MODE);
        //                //#ifdef EHS5AutostartOldInd
        ////#                 _INDICATION.setLed(work_led, Indicator.GREEN);
        ////#                 _INDICATION.set_BlinkSpeed(Indicator.SLO_SPEED);
        //                //#elifdef EHS5DebugOldInd
        ////#                 _INDICATION.setLed(work_led, Indicator.GREEN);
        ////#                 _INDICATION.set_BlinkSpeed(Indicator.SLO_SPEED);
        //                //#elifdef BGS5AutostartOldInd
        ////#                 _INDICATION.setLed(work_led, Indicator.GREEN);
        ////#                 _INDICATION.set_BlinkSpeed(Indicator.SLO_SPEED);
        //                //#elifdef BGS5DebugOldInd
        ////#                 _INDICATION.setLed(work_led, Indicator.GREEN);
        ////#                 _INDICATION.set_BlinkSpeed(Indicator.SLO_SPEED);
        //                //#elifdef TC65Gpio                
        ////#                 _INDICATION.setLed(work_led, Indicator.GREEN);
        ////#                 _INDICATION.set_BlinkSpeed(Indicator.SLO_SPEED);
        //                //#elifdef TC65GpioDebug
        ////#                 _INDICATION.setLed(work_led, Indicator.GREEN);
        ////#                 _INDICATION.set_BlinkSpeed(Indicator.SLO_SPEED);
        //                //#elifdef TC65SpiConcept                
        ////#                 _INDICATION.setLed(work_led, Indicator.GREEN);
        ////#                 _INDICATION.set_BlinkSpeed(Indicator.SLO_SPEED);
        //                //#elifdef TC65SpiConceptDebug
        ////#                 _INDICATION.setLed(work_led, Indicator.GREEN);
        ////#                 _INDICATION.set_BlinkSpeed(Indicator.SLO_SPEED);
        //                //#else
        //                _INDICATION.setLed(work_led, Indicator.GREEN);
        //                //#endif            
        //            }
        //            
        //        }
        else if (msg.indexOf("message,1") >= 0) {
        } else if (msg.indexOf("eons") >= 0) {
            System.out.println("New eons event: " + msg);
            String[] tokens = Parser.GetTokenList(msg);
            for (int i = 0; i < tokens.length; i++) {
                System.out.println("[" + i + "]: " + tokens[i]);
            }
            try {
                if (tokens.length > 3) {
                    if (tokens[4].trim().compareTo("") == 0 || tokens[3].trim().compareTo("\"\"") == 0) {
                        set_JavaTCPbyName(_CURRENT_OPERATOR_NAME.toUpperCase());
                    } else {
                        if(_CURRENT_OPERATOR_NAME.compareTo(tokens[4])!=0){
                        _CURRENT_OPERATOR_NAME = tokens[4];
                        set_JavaTCPbyName(tokens[4].trim().toUpperCase());
                        }
                    }
                }
            } catch (Exception ex) {
            }
        } else if (msg.indexOf("rssi,") >= 0) {
            int rssi = 0;
            String resp = "2G";
            String[] Parse = Parser.Parse(msg, ',');
            if (Parse.length >= 2) {
                try {
                    rssi = Integer.parseInt(Parse[1]);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            //#ifdef EHS
//#             try {
//#                 _ATC.send("AT^SMONI\r",this);
//#             } catch (IllegalStateException ex) {
//#                 ex.printStackTrace();
//#             } catch (IllegalArgumentException ex) {
//#                 ex.printStackTrace();
//#             }
//#             System.out.println(resp);
            //#endif
            switch (_NET_MODE) {
                case CommunicationManager._NET_MODE_2G:
                    break;
                case CommunicationManager._NET_MODE_3G:
                    break;
                case CommunicationManager._NET_MODE_4G:
                    break;
            }
//            if (resp.indexOf("2G") >= 0) {
//                if (_NET_MODE != CommunicationManager._NET_MODE_2G) {
//                    _NET_MODE = CommunicationManager._NET_MODE_2G;
//                }
//            } else if (resp.indexOf("3G") >= 0) {
//                if (_NET_MODE != CommunicationManager._NET_MODE_3G) {
//                    _NET_MODE = CommunicationManager._NET_MODE_3G;
//                }
//            } else if (resp.indexOf("4G") >= 0) {
//                if (_NET_MODE != CommunicationManager._NET_MODE_4G) {
//                    _NET_MODE = CommunicationManager._NET_MODE_4G;
//                }
//            } else {
//                if (_NET_MODE != CommunicationManager._NET_MODE_2G) {
//                    _NET_MODE = CommunicationManager._NET_MODE_2G;
//                }
//            }

//            if (_NET_MODE == CommunicationManager._NET_MODE_2G) {
//
//                //<editor-fold defaultstate="collapsed" desc="Обработка уровня сигнала (rssi) для 2G режима"> 
//                String[] G2tokens = Parser.Parse(msg, ',');
//                for (int i = 0; i < G2tokens.length; i++) {
//                    System.out.println("[" + i + "]: " + G2tokens[i]);
//                }
//                try {
//                    rssi = Integer.parseInt(G2tokens[1].trim());
//                } catch (NumberFormatException ex) {
//                }
//                //</editor-fold>
//
//            } else if (_NET_MODE == CommunicationManager._NET_MODE_3G) {
//
//                //<editor-fold defaultstate="collapsed" desc="Обработка уровня сигнала (rssi) для 3G режима"> 
//                String[] tokens = Parser.Parse(resp, ':');
//                if (tokens.length == 2) {
//                    tokens = Parser.Parse(tokens[1], ',');
//                    for (int i = 0; i < tokens.length; i++) {
//                        System.out.println("[" + i + "]: " + tokens[i]);
//                    }
//                    if (tokens.length < 12) {
//                        return;
//                    }
//
//                    try {
//                        if (resp.indexOf("SEARCH") >= 0) {
//                            rssi = 99;
//                        } else if (tokens[9].compareTo("--") == 0) {
//                            rssi = 99;
//                        } else {
//                            int squal = Integer.parseInt(tokens[9]);
//                            if (squal >= -75) {
//                                rssi = 5;
//                            } else if (squal < -75 && squal >= -85) {
//                                rssi = 4;
//                            } else if (squal < -85 && squal >= -95) {
//                                rssi = 3;
//                            } else if (squal < -95 && squal >= -105) {
//                                rssi = 2;
//                            } else if (squal < -105 && squal >= -110) {
//                                rssi = 1;
//                            } else if (squal < -110) {
//                                rssi = 0;
//                            }
//                        }
//                    } catch (NumberFormatException ex) {
//                    }
//                }
//
//                //</editor-fold>
//            }
            _SIGNAL_LEVEL = rssi * 20;
            System.out.println("[Terminal] rssi: " + rssi);
            System.out.println("[Terminal] signal level in%: " + _SIGNAL_LEVEL);

            switch (_MODE) {
                case CommunicationManager.EXTERNAL_BLOCKED_MODE:
                    System.out.println("[Terminal] MODE state: EXTERNAL_BLOCKED_MODE");
                    break;
                case CommunicationManager.INTERNAL_BLOCKED_MODE:
                    System.out.println("[Terminal] MODE state: INTERNAL_BLOCKED_MODE");
                    break;
                case CommunicationManager.N0RMAL_MODE:
                    System.out.println("[Terminal] MODE state: N0RMAL_MODE");
                    break;
                case CommunicationManager.SMS_ONLY_MODE:
                    System.out.println("[Terminal] MODE state: SMS_ONLY_MODE");
                    break;
            }
            if (_MODE != CommunicationManager.EXTERNAL_BLOCKED_MODE) {
                if (rssi == 0) {//+CIEV: rssi,0
                    if (_SERVICE == 1) {
                        if (_MODE != CommunicationManager.INTERNAL_BLOCKED_MODE) {
                            _OLD_MODE = _MODE;
                        }
                        _MODE = CommunicationManager.INTERNAL_BLOCKED_MODE;
                        //#ifdef EHS5AutostartOldInd
//#                         _INDICATION.setLed(work_led, Indicator.GREEN);
//#                         _INDICATION.set_BlinkSpeed(Indicator.HI_SPEED);
                        //#elifdef EHS5DebugOldInd
//#                         _INDICATION.setLed(work_led, Indicator.GREEN);
//#                     _INDICATION.set_BlinkSpeed(Indicator.HI_SPEED);
                        //#elifdef BGS5AutostartOldInd
//#                         _INDICATION.setLed(work_led, Indicator.GREEN);
//#                         _INDICATION.set_BlinkSpeed(Indicator.HI_SPEED);
                        //#elifdef BGS5DebugOldInd
//#                         _INDICATION.setLed(work_led, Indicator.GREEN);
//#                         _INDICATION.set_BlinkSpeed(Indicator.HI_SPEED);
                        //#elifdef TC65Gpio
//#                         _INDICATION.set_BlinkSpeed(Indicator.HI_SPEED);
//#                         _INDICATION.setLed(work_led, Indicator.GREEN);
                        //#elifdef TC65GpioDebug
//#                         _INDICATION.set_BlinkSpeed(Indicator.HI_SPEED);
//#                         _INDICATION.setLed(work_led, Indicator.GREEN);                        
                        //#else
                        _INDICATION.setLed(work_led, Indicator.RED);
                        //#endif                        
//                        _INDICATION.setLed(work_led, Indicator.RED);
                    }
                } else if (rssi == 1) {//+CIEV: rssi,1
                    if (_SERVICE == 1) {
                        _MODE = _OLD_MODE;
                        //#ifdef EHS5AutostartOldInd
//#                         _INDICATION.setLed(work_led, Indicator.GREEN);
//#                         _INDICATION.set_BlinkSpeed(Indicator.LO_SPEED);
                        //#elifdef EHS5DebugOldInd
//#                         _INDICATION.setLed(work_led, Indicator.GREEN);
//#                         _INDICATION.set_BlinkSpeed(Indicator.LO_SPEED);
                        //#elifdef BGS5AutostartOldInd
//#                         _INDICATION.setLed(work_led, Indicator.GREEN);
//#                         _INDICATION.set_BlinkSpeed(Indicator.LO_SPEED);
                        //#elifdef BGS5DebugOldInd
//#                         _INDICATION.setLed(work_led, Indicator.GREEN);
//#                         _INDICATION.set_BlinkSpeed(Indicator.LO_SPEED);
                        //#elifdef TC65Gpio
//#                         _INDICATION.set_BlinkSpeed(Indicator.LO_SPEED);
//#                         _INDICATION.setLed(work_led, Indicator.GREEN);
                        //#elifdef TC65GpioDebug
//#                         _INDICATION.set_BlinkSpeed(Indicator.LO_SPEED);
//#                         _INDICATION.setLed(work_led, Indicator.GREEN);
                        //#elifdef TC65SpiConcept
//#                         _INDICATION.set_BlinkSpeed(Indicator.LO_SPEED);
//#                         _INDICATION.setLed(work_led, Indicator.GREEN); 
                        //#elifdef TC65SpiConceptDebug
//#                         _INDICATION.set_BlinkSpeed(Indicator.LO_SPEED);
//#                         _INDICATION.setLed(work_led, Indicator.GREEN);                        
                        //#else
                        _INDICATION.setLed(work_led, Indicator.ORANGE);
                        //#endif                        
//                        _INDICATION.setLed(work_led, Indicator.ORANGE);
                    }
                } else if (rssi == 2) {//+CIEV: rssi,2
                    if (_SERVICE == 1) {
                        _MODE = _OLD_MODE;
                        //#ifdef EHS5AutostartOldInd
//#                         _INDICATION.setLed(work_led, Indicator.GREEN);
//#                         _INDICATION.set_BlinkSpeed(Indicator.LO_SPEED);
                        //#elifdef EHS5DebugOldInd
//#                         _INDICATION.setLed(work_led, Indicator.GREEN);
//#                     _INDICATION.set_BlinkSpeed(Indicator.LO_SPEED);
                        //#elifdef BGS5AutostartOldInd
//#                         _INDICATION.setLed(work_led, Indicator.GREEN);
//#                         _INDICATION.set_BlinkSpeed(Indicator.LO_SPEED);
                        //#elifdef BGS5DebugOldInd
//#                         _INDICATION.setLed(work_led, Indicator.GREEN);
//#                         _INDICATION.set_BlinkSpeed(Indicator.LO_SPEED);
                        //#elifdef TC65Gpio    
//#                         _INDICATION.set_BlinkSpeed(Indicator.LO_SPEED);
//#                         _INDICATION.setLed(work_led, Indicator.GREEN);
                        //#elifdef TC65GpioDebug   
//#                         _INDICATION.set_BlinkSpeed(Indicator.LO_SPEED);
//#                         _INDICATION.setLed(work_led, Indicator.GREEN);
                        //#elifdef TC65SpiConcept   
//#                         _INDICATION.set_BlinkSpeed(Indicator.LO_SPEED);
//#                         _INDICATION.setLed(work_led, Indicator.GREEN);
                        //#elifdef TC65SpiConceptDebug
//#                         _INDICATION.set_BlinkSpeed(Indicator.LO_SPEED);
//#                         _INDICATION.setLed(work_led, Indicator.GREEN);                        
                        //#else
                        _INDICATION.setLed(work_led, Indicator.ORANGE);
                        //#endif    
                    }
                } else if (rssi == 3) {//+CIEV: rssi,3
                    if (_SERVICE == 1) {
                        _MODE = _OLD_MODE;
                        //#ifdef EHS5AutostartOldInd
//#                         _INDICATION.setLed(work_led, Indicator.GREEN);
//#                         _INDICATION.set_BlinkSpeed(Indicator.SLO_SPEED);
                        //#elifdef EHS5DebugOldInd
//#                         _INDICATION.setLed(work_led, Indicator.GREEN);
//#                         _INDICATION.set_BlinkSpeed(Indicator.SLO_SPEED);
                        //#elifdef BGS5AutostartOldInd
//#                         _INDICATION.setLed(work_led, Indicator.GREEN);
//#                         _INDICATION.set_BlinkSpeed(Indicator.SLO_SPEED);
                        //#elifdef BGS5DebugOldInd
//#                         _INDICATION.setLed(work_led, Indicator.GREEN);
//#                         _INDICATION.set_BlinkSpeed(Indicator.SLO_SPEED);                        
                        //#elifdef TC65Gpio    
//#                         _INDICATION.set_BlinkSpeed(Indicator.SLO_SPEED);
//#                         _INDICATION.setLed(work_led, Indicator.GREEN);
                        //#elifdef TC65GpioDebug
//#                         _INDICATION.set_BlinkSpeed(Indicator.SLO_SPEED);
//#                         _INDICATION.setLed(work_led, Indicator.GREEN);
                        //#elifdef TC65SpiConcept
//#                         _INDICATION.set_BlinkSpeed(Indicator.SLO_SPEED);
//#                         _INDICATION.setLed(work_led, Indicator.GREEN);
                        //#elifdef TC65SpiConceptDebug
//#                         _INDICATION.set_BlinkSpeed(Indicator.SLO_SPEED);
//#                         _INDICATION.setLed(work_led, Indicator.GREEN);                        
                        //#else
                        _INDICATION.setLed(work_led, Indicator.GREEN);
                        //#endif    
                    }
                } else if (rssi == 4) {//+CIEV: rssi,4
                    if (_SERVICE == 1) {
                        _MODE = _OLD_MODE;
                        //#ifdef EHS5AutostartOldInd
//#                         _INDICATION.setLed(work_led, Indicator.GREEN);
//#                         _INDICATION.set_BlinkSpeed(Indicator.SLO_SPEED);
                        //#elifdef EHS5DebugOldInd
//#                         _INDICATION.setLed(work_led, Indicator.GREEN);
//#                     _INDICATION.set_BlinkSpeed(Indicator.SLO_SPEED);
                        //#elifdef BGS5AutostartOldInd
//#                         _INDICATION.setLed(work_led, Indicator.GREEN);
//#                         _INDICATION.set_BlinkSpeed(Indicator.SLO_SPEED);
                        //#elifdef BGS5DebugOldInd
//#                         _INDICATION.setLed(work_led, Indicator.GREEN);
//#                     _INDICATION.set_BlinkSpeed(Indicator.SLO_SPEED);                        
                        //#elifdef TC65Gpio    
//#                         _INDICATION.set_BlinkSpeed(Indicator.SLO_SPEED);
//#                         _INDICATION.setLed(work_led, Indicator.GREEN);
                        //#elifdef TC65GpioDebug    
//#                         _INDICATION.set_BlinkSpeed(Indicator.SLO_SPEED);
//#                         _INDICATION.setLed(work_led, Indicator.GREEN);
                        //#elifdef TC65SpiConcept
//#                         _INDICATION.set_BlinkSpeed(Indicator.SLO_SPEED);
//#                         _INDICATION.setLed(work_led, Indicator.GREEN);
                        //#elifdef TC65SpiConceptDebug
//#                         _INDICATION.set_BlinkSpeed(Indicator.SLO_SPEED);
//#                         _INDICATION.setLed(work_led, Indicator.GREEN);                        
                        //#else
                        _INDICATION.setLed(work_led, Indicator.GREEN);
                        //#endif    
                    }
                } else if (rssi >= 5 && rssi < 99) {//+CIEV: rssi,5
                    if (_SERVICE == 1) {
                        _MODE = _OLD_MODE;
                        //#ifdef EHS5AutostartOldInd
//#                         _INDICATION.setLed(work_led, Indicator.GREEN);
//#                         _INDICATION.set_BlinkSpeed(Indicator.SLO_SPEED);
                        //#elifdef EHS5DebugOldInd
//#                         _INDICATION.setLed(work_led, Indicator.GREEN);
//#                     _INDICATION.set_BlinkSpeed(Indicator.SLO_SPEED);
                        //#elifdef BGS5AutostartOldInd
//#                         _INDICATION.setLed(work_led, Indicator.GREEN);
//#                         _INDICATION.set_BlinkSpeed(Indicator.SLO_SPEED);
                        //#elifdef BGS5DebugOldInd
//#                         _INDICATION.setLed(work_led, Indicator.GREEN);
//#                     _INDICATION.set_BlinkSpeed(Indicator.SLO_SPEED);                        
                        //#elifdef TC65Gpio    
//#                         _INDICATION.set_BlinkSpeed(Indicator.SLO_SPEED);
//#                         _INDICATION.setLed(work_led, Indicator.GREEN);
                        //#elifdef TC65GpioDebug    
//#                         _INDICATION.set_BlinkSpeed(Indicator.SLO_SPEED);
//#                         _INDICATION.setLed(work_led, Indicator.GREEN);
                        //#elifdef TC65SpiConcept
//#                         _INDICATION.set_BlinkSpeed(Indicator.SLO_SPEED);
//#                         _INDICATION.setLed(work_led, Indicator.GREEN);
                        //#elifdef TC65SpiConceptDebug
//#                         _INDICATION.set_BlinkSpeed(Indicator.SLO_SPEED);
//#                         _INDICATION.setLed(work_led, Indicator.GREEN);
                        //#else
                        _INDICATION.setLed(work_led, Indicator.GREEN);
                        //#endif                        

//                        _INDICATION.setLed(work_led, Indicator.GREEN);
                    }
                } else if (rssi == 99) {//+CIEV: rssi,99
                    if (_SERVICE == 1) {
                        if (_MODE != CommunicationManager.INTERNAL_BLOCKED_MODE) {
                            _OLD_MODE = _MODE;
                        }
                        _MODE = CommunicationManager.INTERNAL_BLOCKED_MODE;
                        //#ifdef EHS5AutostartOldInd
//#                         _INDICATION.setLed(work_led, Indicator.GREEN);
//#                         _INDICATION.set_BlinkSpeed(Indicator.HI_SPEED);
                        //#elifdef EHS5DebugOldInd
//#                         _INDICATION.setLed(work_led, Indicator.GREEN);
//#                         _INDICATION.set_BlinkSpeed(Indicator.HI_SPEED);
                        //#elifdef BGS5AutostartOldInd
//#                         _INDICATION.setLed(work_led, Indicator.GREEN);
//#                         _INDICATION.set_BlinkSpeed(Indicator.HI_SPEED);
                        //#elifdef BGS5DebugOldInd
//#                         _INDICATION.setLed(work_led, Indicator.GREEN);
//#                         _INDICATION.set_BlinkSpeed(Indicator.HI_SPEED);
                        //#elifdef TC65Gpio    
//#                         _INDICATION.set_BlinkSpeed(Indicator.HI_SPEED);
//#                         _INDICATION.setLed(work_led, Indicator.GREEN);
                        //#elifdef TC65GpioDebug    
//#                         _INDICATION.set_BlinkSpeed(Indicator.HI_SPEED);
//#                         _INDICATION.setLed(work_led, Indicator.GREEN);
                        //#elifdef TC65SpiConcept
//#                         _INDICATION.set_BlinkSpeed(Indicator.HI_SPEED);
//#                         _INDICATION.setLed(work_led, Indicator.GREEN);
                        //#elifdef TC65SpiConceptDebug
//#                         _INDICATION.set_BlinkSpeed(Indicator.HI_SPEED);
//#                         _INDICATION.setLed(work_led, Indicator.GREEN);
                        //#else
                        _INDICATION.setLed(work_led, Indicator.RED);
                        //#endif                        

//                        _INDICATION.setLed(work_led, Indicator.RED);
                    }
                }
            }
            switch (_MODE) {
                case CommunicationManager.EXTERNAL_BLOCKED_MODE:
                    System.out.println("MODE state: EXTERNAL_BLOCKED_MODE");
                    break;
                case CommunicationManager.INTERNAL_BLOCKED_MODE:
                    System.out.println("MODE state: INTERNAL_BLOCKED_MODE");
                    break;
                case CommunicationManager.N0RMAL_MODE:
                    System.out.println("MODE state: N0RMAL_MODE");
                    break;
                case CommunicationManager.SMS_ONLY_MODE:
                    System.out.println("MODE state: SMS_ONLY_MODE");
                    break;
            }
        }
    }

    private void user_handler(String msg) {
        System.out.println("New user event:" +msg);
    }

    private void serv_handler(String msg) {
        System.out.println("New server event: "+msg);
    }

    //получить текущий вызов
//    private Call get_Call() {
//        Call res = new Call();
//        StringBuffer bf = new StringBuffer();
//        System.out.println("Get call");
//        try {
//            bf.append(_ATC.send("AT+CLCC\r").trim());
//            String[] tokens = StringPraser.split(bf.toString());
//            System.out.println("Size: " + tokens.length);
//            for (int i = 0; i < tokens.length; i++) {
//                System.out.println("[" + i + "]:" + tokens[i].trim());
//            }
//            switch (tokens.length) {
//                case 0:
//                    res = null;
//                    break;
//                case 9:
//                    System.out.println("case 9: ");
//                    res.INDEX = (char) Integer.parseInt(tokens[1].trim());
//                    res.DIRECTION = (char) Integer.parseInt(tokens[2].trim());
//                    res.STATE = (char) Integer.parseInt(tokens[3].trim());
//                    res.MODE = (char) Integer.parseInt(tokens[4].trim());
//                    res.MPTY = (char) Integer.parseInt(tokens[5].trim());
//                    res.NUMBER = tokens[6].trim();
//                    res.TYPE = (char) Integer.parseInt(tokens[7].trim());
//                    res.NAME = "";
//                    break;
//                case 10:
//                    System.out.println("case 10: ");
//                    res.INDEX = (char) Integer.parseInt(tokens[1].trim());
//                    res.DIRECTION = (char) Integer.parseInt(tokens[2].trim());
//                    res.STATE = (char) Integer.parseInt(tokens[3].trim());
//                    res.MODE = (char) Integer.parseInt(tokens[4].trim());
//                    res.MPTY = (char) Integer.parseInt(tokens[5].trim());
//                    res.NUMBER = tokens[6].trim();
//                    res.TYPE = (char) Integer.parseInt(tokens[7].trim());
//                    res.NAME = tokens[8].trim();
//                    break;
//                default:
//                    System.out.println("default : ");
//                    res = null;
//                    break;
//            }
//
//        } catch (ATCommandFailedException ex) {
//        } catch (IllegalStateException ex) {
//        } catch (IllegalArgumentException ex) {
//        }
//        if (res != null) {
//            if (res.STATE == 0x02) {
//                _INDICATION.blinkOff(call_led);
//                _INDICATION.setLed(call_led, Indicator.GREEN);
//            } else if (res.STATE == 0) {
//                _INDICATION.binkOn(call_led);
//            }
//        }
//        return res;
//    }
    private Call parse_Call_Info(String call) {
        Call res = new Call();
        StringBuffer bf = new StringBuffer();

        try {
            String[] tokens = StringPraser.split(call);
            System.out.println("Size: " + tokens.length);
            for (int i = 0; i < tokens.length; i++) {
                System.out.println("[" + i + "]:" + tokens[i].trim());
            }
            switch (tokens.length) {
                case 0:
                    res = null;
                    break;
                case 9:
                    System.out.println("case 9: ");
                    res.INDEX = (char) Integer.parseInt(tokens[1].trim());
                    res.DIRECTION = (char) Integer.parseInt(tokens[2].trim());
                    res.STATE = (char) Integer.parseInt(tokens[3].trim());
                    res.MODE = (char) Integer.parseInt(tokens[4].trim());
                    res.MPTY = (char) Integer.parseInt(tokens[5].trim());
                    res.NUMBER = tokens[6].trim();
                    res.TYPE = (char) Integer.parseInt(tokens[7].trim());
                    res.NAME = "";
                    break;
                case 10:
                    System.out.println("case 10: ");
                    res.INDEX = (char) Integer.parseInt(tokens[2].trim());
                    res.DIRECTION = (char) Integer.parseInt(tokens[3].trim());
                    res.STATE = (char) Integer.parseInt(tokens[4].trim());
                    res.MODE = (char) Integer.parseInt(tokens[5].trim());
                    res.MPTY = (char) Integer.parseInt(tokens[6].trim());
                    res.NUMBER = tokens[7].trim();
                    res.TYPE = (char) Integer.parseInt(tokens[8].trim());
                    if (tokens[9].trim().indexOf("OK") > -1) {
                        res.NAME = "";
                    } else {
                        res.NAME = tokens[9].trim();
                    }
                    break;
                case 11:
                    System.out.println("case 11: ");
                    res.INDEX = (char) Integer.parseInt(tokens[2].trim());
                    res.DIRECTION = (char) Integer.parseInt(tokens[3].trim());
                    res.STATE = (char) Integer.parseInt(tokens[4].trim());
                    res.MODE = (char) Integer.parseInt(tokens[5].trim());
                    res.MPTY = (char) Integer.parseInt(tokens[6].trim());
                    res.NUMBER = tokens[7].trim();
                    res.TYPE = (char) Integer.parseInt(tokens[8].trim());
                    res.NAME = tokens[9].trim();
                    break;
                default:
                    System.out.println("default : ");
                    res = null;
                    break;
            }

        } catch (IllegalStateException ex) {
        } catch (IllegalArgumentException ex) {
        }
        if (res != null) {
            if (res.STATE == 0x02) {
                _INDICATION.blinkOff(call_led);
                _INDICATION.setLed(call_led, Indicator.GREEN);
            } else if (res.STATE == 0) {
                _INDICATION.binkOn(call_led);
            }
        }
        return res;

    }

    //проверить входящий вызов
    private boolean check_call(Call call) {
        System.out.println("Check call");
        boolean res = false;
        int whiteindex = 0;
        int sosindex = 0;
        int remoteindex = 0;
        if (call == null) {
            return false;
        }
        whiteindex = _WHITE_LIST.indexOf(call.NUMBER);
        sosindex = _SOS_LIST.indexOf(call.NUMBER);

        res = whiteindex != -1 || sosindex != -1;
        return res;
    }

    private void set_Call(Call call) { //Установка объекта Call для последующего формирования сигнала
        System.out.println("Set call");
        int whiteindex = -1;
        int sosindex = -1;
        int remoteindex = -1;
        int rebootindex = -1;
        if (call == null) {
            System.out.println("set_Call(Call call): call is null");
            return;
        }
        whiteindex = _WHITE_LIST.indexOf(call.NUMBER);
        sosindex = _SOS_LIST.indexOf(call.NUMBER);
        rebootindex = _RESTART_BY_CALL_LIST.indexOf(call.NUMBER);
        System.out.println("Call number indexess: sos=" + sosindex + " white=" + whiteindex + " reboot=" + rebootindex);
        if (sosindex > -1) {
            this._LAST_CALL = call;
            this._LAST_CALL.EXTENDED_INDEX = sosindex + 1;
        } else if (whiteindex > -1) {//непонятная хрень!!!!!!!!!!!
            this._LAST_CALL = call;
            this._LAST_CALL.EXTENDED_INDEX = whiteindex + 1;
        } else if (rebootindex > -1) {
            this._LAST_CALL = call;
            this._LAST_CALL.EXTENDED_INDEX = rebootindex + 1;
        } else {
            this._LAST_CALL = null;
        }
    }

    //Сбросить вызов с указанием причины
    private boolean hungup_call(Call call, char reason) {
        boolean res = true;

        String strres = "";
        try {
            _ATC.send("AT^SHUP=" + (int) reason + ',' + call.INDEX + '\r', this);
//            if (strres.indexOf("OK") >= 0) {
//                res = true;
//            }
        } catch (IllegalStateException ex) {
        } catch (IllegalArgumentException ex) {
        }
        disable_Audio();
        return res;
    }

    //Ответить на входящий вызов
    private boolean answer_on_call(Call call) {
        boolean res = true;

        enable_Audio();

        try {
            _ATC.send("AT^SRTC=1,4\r", this);
        } catch (IllegalStateException ex) {
        } catch (IllegalArgumentException ex) {
        }

        try {
            synchronized (_MUTEX) {
                _MUTEX.wait(3000);
                _MUTEX.notifyAll();
            }
        } catch (InterruptedException ex) {
        }
        try {
            _ATC.send("ATA\r", this);
//            if (_ATC.send("ATA\r",this)) 
//            {
//                res = true;
//            }

        } catch (IllegalStateException ex) {
        } catch (IllegalArgumentException ex) {
        }

        return res;
    }

    //обработчик новых пакетов от АК

    /**
     *
     * @param command
     */
    public void newPacketReccived(Object command) {
        System.out.println("Terminal: " + ((AkMessage) command).toString());
        System.out.println("Terminal: Add message to queue");
        if (command.getClass().equals(AkMessage.class)) {
            if (((AkMessage) command).get_AkNumber() != _ObjectID) {
                System.out.println("Try update config");
                String url = _CURRENT_CONFIG.optString("UpdateUrl");
                if (url.compareTo("") != 0) {
                    JSONObject cfg = obtainConfig(((AkMessage) command).get_AkNumber(), url);
                    if (cfg != null) {
                        _CURRENT_CONFIG = cfg;
                        _FAR_WAYS.clear();
                        acceptConfig(_CURRENT_CONFIG, true);
                    }
                }
            }
            synchronized (_MESSAGES) {
                _MESSAGES.addElement(command);
                _MESSAGES.notifyAll();
            }
        }

    }

    //Установка настроек для интернета по имени оператора
    private boolean set_JavaTCPbyName(String opname) {
        if (_JAVA_NET && _CURRENT_OPERATOR_NAME.compareTo(opname) == 0 && _JAVA_NET_NAME.compareTo(opname)==0) {
            System.out.println("set_JavaTCPbyName: _JAVA_NET = "+_JAVA_NET + "  _CURRENT_OPERATOR_NAME = "+_CURRENT_OPERATOR_NAME);
            return false;
        }
        System.out.println("set_JavaTCPbyName(" + opname + ")");
        StringBuffer buf = new StringBuffer();
        boolean res = false;
        String connProfile = "";
        String answer = "ERROR";
        SMSMessage smsm;
        if (opname == null) {
            return set_JavaTCPDefault();
        } else if (opname.compareTo("") == 0) {
            return set_JavaTCPDefault();
        }
        Vector settings = new Vector(0);
        try {
            settings = getOpSettings(opname);
        } catch (IOException ex) {
        }
        if (settings.isEmpty()) {
            return false;
        }
        buf.append(((Vector) settings.elementAt(0)).elementAt(0).toString());
        buf.append(",");
        buf.append(((Vector) settings.elementAt(0)).elementAt(1).toString());
        buf.append(",");
        buf.append(((Vector) settings.elementAt(0)).elementAt(2).toString());

        //"gprs","inet.ycc.ru","motiv","motiv",,60
        String command = "AT^SJNET=\"gprs\"," + buf.toString()+ ",," + "5" + '\r';//+ ",," + "60" + 
        _ATC.send(command, this);
        char cnt = 0;
//        while (cnt < 4) {//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
//            try {
//                _ATC.send(command,this);
//                System.out.println(command + ": " + answer);
////                if (answer.indexOf("OK") >= 0) {
////                    res = true;
////                    break;
////                }
//            }catch (IllegalStateException ex) {
//                ex.printStackTrace();
//            } catch (IllegalArgumentException ex) {
//                ex.printStackTrace();
//            }
//            cnt++;
//            synchronized(_MUTEX){
//                try {
//                    _MUTEX.wait(1000);
//                    
//                } catch (InterruptedException ex) {
//                    ex.printStackTrace();
//                }
//                _MUTEX.notifyAll();
//            }
//        }
        return res;
    }

    //Установка настроек для интернета в ручную.
    private boolean set_JavaTCP(String settings) {
        System.out.println("set_JavaTCP(String settings)");
        String connProfile = "";
        String answer = "";
        boolean res = false;

        return res;
    }

    private boolean set_JavaTCPDefault() {
        System.out.println("set_JavaTCPDefault()");
        if(_CURRENT_CONFIG!=null){
            JSONObject syst = _CURRENT_CONFIG.optJSONObject("System");
            _CURRENT_OPERATOR_NAME=syst.optString("defOperator","MEGAFON");
        }else{_CURRENT_OPERATOR_NAME = "MEGAFONS";};
        return set_JavaTCPbyName(_CURRENT_OPERATOR_NAME);
    }

    /**
     *
     * @param opname
     * @return
     * @throws IOException
     */
    public Vector getOpSettings(String opname) throws IOException {
        Vector res = new Vector(0);
        String op = "";
        opname = opname.toUpperCase().trim();
        System.out.println("getOpSettings: "+opname);
        if (opname.indexOf("BEELINE") >= 0) {
            op = "BEELINE";
        } else if (opname.indexOf("MEGAFON") >= 0) {
            op = "MEGAFON";
        } else if (opname.indexOf("MOTIV") >= 0) {
            op = "MOTIV";
        } else if (opname.indexOf("MTS") >= 0) {
            op = "MTS";
        } else if (opname.indexOf("CC 250 NC 35") >= 0) {
            op = "CC250NC35";
        } else if (opname.indexOf("CC250NC35") >= 0) {
            op = "CC250NC35";
        } else if (opname.indexOf("RUSEC") >= 0) {
            op = "RUSEC";
        } else if (opname.indexOf("RUSUT") >= 0 || opname.indexOf("RUS UT") >= 0 || opname.indexOf("RUS17") >= 0 || opname.indexOf("RUS 17") >= 0) {
            op = "USI";
        } else if (opname.indexOf("TELE2") >= 0) {
            op = "TELE2";
        } else if (opname.indexOf("RUSSIA VOTEK MOBILE") >= 0) {
            op = "TELE2";
        }
        if (op.compareTo("") == 0) {
            return res;
        }
        String sprofile = readSettingsString("/settings/operators/" + op + "/profile");

        String sbalans = readSettingsString("/settings/operators/" + op + "/balans");

        res.addElement(StringPraser.ParseVector(sprofile.trim()));
        res.addElement(sbalans.trim());
        return res;
    }

    /**
     *
     * @param url
     * @return
     * @throws IOException
     */
    public String readSettingsString(String url) throws IOException {
        String res = "";
        InputStream value = null;
        byte[] bvalue;
        try {
            value = System.class.getResourceAsStream(url);
           
            bvalue = new byte[value.available()];
            if (value.available() > 0) {
                if (value.read(bvalue) > 0) {
                    res = new String(bvalue);
                }

            }
            value.close();
        } catch (IOException ex) {
            if (value != null) {
                value.close();
            }
        }

        value = null;
        bvalue = null;
        return res;
    }

//    public String getCurrOpName() throws ATCommandFailedException, IllegalStateException, Exception, Error {
//        String res = "";
//        String answer = "";
//        if (_ATC == null) {
//            System.out.println("ATCommand is NULL");
//            return "";
//        }
//        int state = 0;
//        try {
////            synchronized (_ATC) {
////                state=1;
////                
////                _ATC.notifyAll();
////            }
////            answer = answer.trim();
////            if (answer.indexOf("OK") < 0) {
////                 System.out.println("ATCommand wrong answer: "+answer);
////                return "";
////            }
//            {
//               
//                _ATC.send("ATE1\r", this);
//                _ATC.send("AT+COPS=0\r", this);
//                _ATC.send("AT+COPS?\r", this);
//                
//
//            }
//            answer = answer.trim();
//            if (answer.indexOf(",\"") >= 0) {
//                res = answer.substring(answer.indexOf(",\"") + 2, answer.indexOf("\",")).trim();
//            }
//
//        } catch (IllegalStateException ex) {
//            throw new ATCommandFailedException(ex.getMessage() + "->Terminal.getCurrOpName()");
//        } catch (Exception ex) {
//            throw new Exception(ex.getMessage() + "->Terminal.getCurrOpName()state: " + state);
//        } catch (Error err) {
//            throw new Error(err.getMessage() + "->Terminal.getCurrOpName()");
//        }
//        return res;
//    }

    /**
     *
     * @return
     * @throws ATCommandFailedException
     * @throws IllegalStateException
     * @throws Exception
     * @throws Error
     */
    public int getCurrOpID() throws ATCommandFailedException, IllegalStateException, Exception, Error {
        int res = -1;
        String answer = "";
        try {
//            /*synchronized(System.out)*/ {
//                System.out.println("Try to obtain operator id");
//                System.out.println("Send AT+COPS=3,2");
//                /*System.out.notifyAll();*/
//            }
            {
                _ATC.send("AT+COPS=3,2\r", this);
                _ATC.send("AT+COPS?\r", this);

            }
//            /*synchronized(System.out)*/ {
//                System.out.println("Answer for AT+COPS=3,2: "+answer);
//                /*System.out.notifyAll();*/
//            }
//            answer = answer.trim();
//            if (answer.indexOf("OK") < 0) {
//                return -1;
//            }
////            /*synchronized(System.out)*/ {
////                System.out.println("Send AT+COPS?");
////                /*System.out.notifyAll();*/
////            }            
//            {
//                
//
//            }
//            /*synchronized(System.out)*/ {
//                System.out.println("Answer for  AT+COPS?: "+answer );
//                /*System.out.notifyAll();*/
//            }               
            answer = answer.trim();
            if (answer.indexOf(',') >= 0) {
                //#ifdef BGS
//# //                answer = answer.trim().substring(answer.indexOf(",\"")+2, answer.indexOf('\r')-3).trim();
//# //                /*synchronized(System.out)*/ {
//# //                    System.out.println("Parse answer");
//# //                    /*System.out.notifyAll();*/
//# //                }
//#                 answer = Parser.Parse(answer, ',')[2].trim();
//#                 answer = answer.substring(1, answer.length() - 1).trim();
//# //                /*synchronized(System.out)*/ {
//# //                    System.out.println("Answer: " + answer);
//# //                    /*System.out.notifyAll();*/
//# //                }
                //#else
//                /*synchronized(System.out)*/ {
//                    System.out.println("Parse answer (not for bgs5)");
//                    /*System.out.notifyAll();*/
//                }
                answer = answer.substring(answer.indexOf(",\"") + 2, answer.indexOf('\r') - 1);
                //#endif
                res = Integer.parseInt(answer.trim());
            }
            {
                _ATC.send("AT+COPS=0\r", this);

            }
            answer = answer.trim();
        } catch (IllegalStateException ex) {
            throw new ATCommandFailedException(ex.getMessage() + "->Terminal.getCurrOpID()");
        } catch (Exception ex) {
            throw new Exception(ex.getMessage() + "->Terminal.getCurrOpID()");
        } catch (Error err) {
            throw new Error(err.getMessage() + "->Terminal.getCurrOpID()");
        }
        return res;
    }

    /**
     *
     */
    public void getCurState() {
        System.out.println("getCurState()");
        _ATC.send("AT^SIND?\r", this);
        System.out.println("end of getCurState()");
    }

    /**
     *
     * @param event
     */
    public void externalEvent(Object event) {
        System.out.println("External event!!! " + event.getClass().getName());
        oko3c.al.communicate.ak.OKO2Mes o2m = new OKO2Mes();
        if (event.getClass().equals(SMSMessage.class)) {
            if (((SMSMessage) event).Text.trim().endsWith("\0D")) {
                ((SMSMessage) event).Text = ((SMSMessage) event).Text.trim().substring(0, ((SMSMessage) event).Text.trim().indexOf("\0D"));
            }
            if (((SMSMessage) event).Text.trim().endsWith("\\0D")) {
                ((SMSMessage) event).Text = ((SMSMessage) event).Text.trim().substring(0, ((SMSMessage) event).Text.trim().indexOf("\\0D"));
            }

            System.out.println(((SMSMessage) event).Text.trim());
            if (((SMSMessage) event).Text.trim().indexOf("9:") >= 0) {
                handleCmCo(((SMSMessage) event).Text.trim());
                return;
            } else {
                o2m.set_Channel(OKO2Mes.SMS_CHANNEL);
                if (!o2m.fromString(((SMSMessage) event).Text.trim())) {
                    System.out.println("Error in o2m.fromString()");
                    return;
                }
            }
        } else if (event.getClass().equals(OKO2Mes.class)) {
            o2m = (OKO2Mes) event;
        }

        //if(o2m.get_Channel()==OKO2Mes.SMS_CHANNEL && o2m.get_Oko2Pass().compareTo(_WorkDir));
        {

            //<editor-fold defaultstate="collapsed" desc="Обработка OKO2 сообщения">
            int ak = o2m.get_AkNumber();
            if (ak != this._ObjectID) {
                System.out.println("Wrong AK number!");
                return;
            }
            //String pass = o2m.get_Oko2Pass();
            int evcl = o2m.get_EventClass();
            int evcod = o2m.get_EventCod();
            int part = o2m.get_PartitionNumber();
            int zone = o2m.get_ZoneNumber();
            if (evcl == 13) {
                if (evcod == 1) {
                    if (zone < 1 || zone > 6) {
                        return;
                    }
                    switch (part) {
                        case 0:
                            _INDICATION.setLed((byte) (zone + 1), Indicator.GREEN);
                            break;
                        case 1:
                            _INDICATION.setLed((byte) (zone + 1), Indicator.RED);
                            break;
                        case 2:
                            _INDICATION.setLed((byte) (zone + 1), Indicator.RED);
                            break;
                    }
                }
            } else if (evcl == 8) {
                switch (evcod) {
                    case 1:
                        switch (part) {
                            case 0:
                                if (zone == 0) {
                                    new Timer().schedule(new Task(this, new GetStateEvent(), "GetState"), 1000);
                                }
                                break;
                        }   break;
                    case 5:
                        switch (part) {
                            case 0:
                                //Перезагрузка прибора
                                this.reboot();
                                break;
                            case 1:
                                
                                System.out.println("Set N0RMAL_MODE");
                                new Timer().schedule(new Task(this, new UnblockEvent(), "Unblock"), 1000);
                                break;
                            case 2:
                                System.out.println("Set BLOCKED_MODE");
                                if (_MODE == CommunicationManager.EXTERNAL_BLOCKED_MODE) {
                                    break;
                                } else {
                                    AkMessage2 block_message = new AkMessage2();
                                    block_message.set_AkNumber(_ObjectID);
                                    block_message.set_EventClass(5);
                                    block_message.set_EventCod(7);
                                    block_message.set_MesNumber(get_MessageNumber());
                                    block_message.set_PartitionNumber(0);
                                    block_message.set_ZoneNumber(1);
                                    synchronized (_MESSAGES) {
                                        _MESSAGES.addElement(block_message);
                                        _MESSAGES.notifyAll();
                                    }
                                    //2:070035-07003-000-008-005-002-000
                                    new Timer().schedule(new Task(this, new BlockEvent(), "Block"), 30000);
                                    System.out.println("Exit from Set BLOCKED_MODE section");
                                }
                                break;
                            case 3:
                                break;
                            case 4:
                                break;
                            case 5:
                                break;
                            case 6:
                                break;
                        }   break;
                    case 6:
                        break;
                    case 100:
                        if (zone == 2) {
                            String url = _CURRENT_CONFIG.optString("UpdateUrl");
                            System.out.println(url);
                            new Timer().schedule(new Task(this, new UpdateFirmwareEvent(), "Update"), 2000);
                        } else if (zone == 3) {
                            //Обновить конфигурацию с сервера
                            System.out.println("Try update config");
                            System.out.println("Try update config");
                            Task updcfg = new Task(this, new UpdateConfigEvent(), "UpdateConfig");
                            new Timer().schedule(new Task(this, new UpdateConfigEvent(), "UpdateConfig"), 1000);
                        }   break;
                    default:
                        break;
                }
            }
            //</editor-fold>

        }

    }

    private void handleCmCo(String command) {
        System.out.println("Try handle sms command");
        //Обработка управляющих команд
        if (command.endsWith("\0D")) {
            command = command.substring(0, command.indexOf("\0D"));
        }
        if (command.endsWith("\\0D")) {
            command = command.substring(0, command.indexOf("\\0D"));
        }
        System.out.println(command);
        String[] Parse = Parser.Parse(command.trim(), ':');
        com.oko.lang.System.print(Parse);
        System.out.println("Analayze type of command. Lenght: " + Parse.length);
        if (Parse.length != 2) {
            return;
        }
        if (Parse[0].compareTo("9") == 0) {
            String[] tokens = Parser.Parse(Parse[1], '-');
            com.oko.lang.System.print(tokens);
            if (tokens.length != 4) {
                return;
            }
            System.out.println("Analayze parametre number");
            if (tokens[0].compareTo("87") == 0) {
                System.out.println("Analayze commande code");
                if (tokens[1].compareTo("259") == 0) {
                    //Установить номер объекта и обновить конфиг
                    JSONObject syst = _CURRENT_CONFIG.optJSONObject("System");
                    String spassword = syst.optString("password", "");
                    System.out.println("Analayze password");
                    if (tokens[2].compareTo(spassword) == 0) {
                        try {
                            System.out.println("Analayze data");
                            System.out.println(tokens[3].trim());
                            byte bytes[] = tokens[3].trim().getBytes();
                            com.oko.lang.System.print(bytes);
                            this._ObjectID = Integer.parseInt(tokens[3].trim());

                            System.out.println("Try update config");
                            new Timer().schedule(new Task(this, new UpdateConfigEvent(), "UpdateConfig"), 1000);
                        } catch (Exception ex) {
                        }
                    }
                }
            }
        }
    }

    /**
     *
     * @param event
     */
    public void internalEvent(Object event) {
        System.out.println("InternalEvent: " + event.getClass().getName());
        int sigl = 0;
        if (_SIGNAL_LEVEL > 100) {
            sigl = 0x7F;
        } else {
            sigl = Util.setBit(_SIGNAL_LEVEL, 7, false);
        }
//        for (int i = 0; i < 50; i++) {
//            try {
//                synchronized (_MUTEX) {
//                    _MUTEX.wait(1000);
//                    _MUTEX.notifyAll();
//                }
//                sigl = _MONITOR.checkSigLevel();
//                if (sigl < 0) {
//                    sigl = 0;
//                }
//                if (sigl == 0) {
//                    continue;
//                }
//                break;
//            } catch (IllegalStateException ex) {
//                continue;
//            } catch (IllegalMonitorStateException ex) {
//                continue;
//            } catch (Exception ex) {
//                continue;
//            } catch (Error ex) {
//                continue;
//            }
//        }
        System.out.println("Signal level: " + _SIGNAL_LEVEL);
        if (event.getClass().equals(SysStartEvent.class)) {
            _SYSSTART=true;
            _PBREADY=true;
        }
        else if (event.getClass().equals(LiveEvent.class)) {

            AkMessage2 m = new AkMessage2();
            m.set_AkNumber(_ObjectID);
            m.set_EventClass(3);
            m.set_EventCod(21);

            m.set_ZoneNumber(0);
            m.set_PartitionNumber(sigl);
            m.set_MesNumber(get_MessageNumber());
            System.out.println(m.toOko2String());
            synchronized (_MESSAGES) {
                _MESSAGES.addElement(m);
                _MESSAGES.notifyAll();
            }
            this._LAST_LIVE_EVENT = System.currentTimeMillis();

        } else if (event.getClass().equals(EmRestGsmEvent.class)) {
            System.out.println("New EmRestGsmEvent!");
//            if (!this._RESTART_GSM) 
            {
                if(_STATE!=_RESTART_GSM_STATE){
                    _FAR_WAYS.set_Mode(CommunicationManager.INTERNAL_BLOCKED_MODE);
                    _FAR_WAYS.pause();
                    _STATE=_RESTART_GSM_STATE;
                }
//                if (reinitGsm() == -1) {
//                    ATEvent("+CIEV: service,0");
//                }
            }
            return;
        } else if (event.getClass().equals(RstGsmEvent.class)) {
            System.out.println("New RstGsmEvent!");
//            if (!this._RESTART_GSM) 
            {
                if(_STATE!=_RESTART_GSM_STATE){
                    _FAR_WAYS.set_Mode(CommunicationManager.INTERNAL_BLOCKED_MODE);
                    _FAR_WAYS.pause();
                    _STATE=_RESTART_GSM_STATE;
                }
            }
            return;
        } else if (event.getClass().equals(BlockEvent.class)) {
            if (this._MODE == CommunicationManager.EXTERNAL_BLOCKED_MODE) {
                return;
            }
            
            try {
                if (_TIMER_LIVE != null) {
                    _TIMER_LIVE.cancel();
                    _TIMER_LIVE = null;
                }
            } catch (Exception ex) {

            }            
            if (_MODE != CommunicationManager.EXTERNAL_BLOCKED_MODE) {
                _OLD_MODE = _MODE;
            }
            
            this._MODE = CommunicationManager.EXTERNAL_BLOCKED_MODE;
            this._FAR_WAYS.set_Mode(_MODE);
            this._COM.set_Mode(_MODE);
            this._PacketHandler.set_Mode(_MODE);
            saveMODE();

            //#ifdef EHS5AutostartOldInd
//#             _INDICATION.setLed(work_led, Indicator.GREEN);
//#             _INDICATION.set_BlinkSpeed(Indicator.HI_SPEED);
            //#elifdef EHS5DebugOldInd
//#             _INDICATION.setLed(work_led, Indicator.GREEN);
//#             _INDICATION.set_BlinkSpeed(Indicator.HI_SPEED);
            //#elifdef BGS5AutostartOldInd
//#             _INDICATION.setLed(work_led, Indicator.GREEN);
//#             _INDICATION.set_BlinkSpeed(Indicator.HI_SPEED);
            //#elifdef BGS5DebugOldInd
//#             _INDICATION.setLed(work_led, Indicator.GREEN);
//#             _INDICATION.set_BlinkSpeed(Indicator.HI_SPEED);            
            //#elifdef TC65Gpio
//#             _INDICATION.set_BlinkSpeed(Indicator.HI_SPEED);
//#             _INDICATION.setLed(work_led, Indicator.GREEN);
            //#elifdef TC65GpioDebug
//#             _INDICATION.set_BlinkSpeed(Indicator.HI_SPEED);
//#             _INDICATION.setLed(work_led, Indicator.GREEN);            
            //#else 
            _INDICATION.setLed(work_led, Indicator.RED);
            //#endif

            for (byte i = 2; i < 8; i++) {
                _INDICATION.setLed(i, Indicator.OFF);
            }
            
        } else if (event.getClass().equals(UnblockEvent.class)) {
            if (_OLD_MODE != CommunicationManager.EXTERNAL_BLOCKED_MODE) {
                _MODE = _OLD_MODE;
            } else {
                _MODE = CommunicationManager.N0RMAL_MODE;
                _OLD_MODE = CommunicationManager.N0RMAL_MODE;
            }
            saveMODE();
            this._FAR_WAYS.set_Mode(_MODE);
            this._COM.set_Mode(_MODE);
            this._PacketHandler.set_Mode(_MODE);
            try {
                if (_TIMER_LIVE != null) {
                    _TIMER_LIVE.cancel();
                    _TIMER_LIVE = null;
                }
                if (_TIMER_LIVE == null) {
                    _TIMER_LIVE = new Timer();
                }
//                _TIMER_LIVE.schedule(new Task(this, new LiveEvent(), "Live"), 20000, _LIVE_PERIOD * 60 * 60 * 1000);//часы*минуты*секунды*миллисекунды
                _TIMER_LIVE.schedule(new Task(this, new LiveEvent(), "Live"), _LIVE_PERIOD * 60 * 60 * 1000, _LIVE_PERIOD * 60 * 60 * 1000);//часы*минуты*секунды*миллисекунды

            } catch (Exception ex) {
                System.out.println("AcceptConfig-LivePeriod");
            } catch (Error err) {
            }
            //#ifdef EHS5AutostartOldInd
//#             _INDICATION.setLed(work_led, Indicator.GREEN);
//#             _INDICATION.set_BlinkSpeed(Indicator.SLO_SPEED);
            //#elifdef EHS5DebugOldInd
//#             _INDICATION.setLed(work_led, Indicator.GREEN);
//#             _INDICATION.set_BlinkSpeed(Indicator.SLO_SPEED);
            //#elifdef BGS5AutostartOldInd
//#             _INDICATION.setLed(work_led, Indicator.GREEN);
//#             _INDICATION.set_BlinkSpeed(Indicator.SLO_SPEED);
            //#elifdef BGS5DebugOldInd
//#             _INDICATION.setLed(work_led, Indicator.GREEN);
//#             _INDICATION.set_BlinkSpeed(Indicator.SLO_SPEED);            
            //#elifdef TC65Gpio
//#             _INDICATION.set_BlinkSpeed(Indicator.SLO_SPEED);
//#             _INDICATION.setLed(work_led, Indicator.GREEN);
            //#elifdef TC65GpioDebug
//#             _INDICATION.set_BlinkSpeed(Indicator.SLO_SPEED);
//#             _INDICATION.setLed(work_led, Indicator.GREEN);            
            //#else
            _INDICATION.setLed(work_led, Indicator.GREEN);
            //#endif            

            for (byte i = 2; i < 8; i++) {
                _INDICATION.setLed(i, Indicator.GREEN);
            }

            AkMessage2 unblock_message = new AkMessage2();
            unblock_message.set_AkNumber(_ObjectID);
            unblock_message.set_EventClass(5);
            unblock_message.set_EventCod(7);
            unblock_message.set_MesNumber(get_MessageNumber());
            unblock_message.set_PartitionNumber(0);
            unblock_message.set_ZoneNumber(0);
            reset_MessageNumbers();

            AkMessage2 start_system = new AkMessage2();
            start_system.set_AkNumber(_ObjectID);
            start_system.set_EventClass(3);
            start_system.set_EventCod(5);
            start_system.set_MesNumber(0);

            AkMessage2 m = new AkMessage2();
            m.set_AkNumber(_ObjectID);
            m.set_EventClass(3);
            m.set_EventCod(21);
            sigl = Util.setBit(sigl, 7, false);
            m.set_ZoneNumber(0);
            m.set_PartitionNumber(sigl);
            m.set_MesNumber(get_MessageNumber());
//                            System.out.println(m.toOko2String());
            synchronized (_MESSAGES) {

                _MESSAGES.addElement(unblock_message);
                _MESSAGES.addElement(start_system);
                _MESSAGES.addElement(m);
                _MESSAGES.notifyAll();
            }
        } else if (event.getClass().equals(DisableAudio.class)) { //отключить аудио
            disable_Audio();
        } else if (event.getClass().equals(GetStateEvent.class)) {
            if (this._MODE == CommunicationManager.EXTERNAL_BLOCKED_MODE) {
                return;
            }
            AkMessage2 m = new AkMessage2();
            m.set_AkNumber(_ObjectID);
            m.set_EventClass(3);
            m.set_EventCod(21);
            sigl = Util.setBit(sigl, 7, false);
            m.set_ZoneNumber(0);
            m.set_PartitionNumber(sigl);
            m.set_MesNumber(get_MessageNumber());
            synchronized (_MESSAGES) {
                _MESSAGES.addElement(m);
                _MESSAGES.notifyAll();
            }
        } else if (event.getClass().equals(UpdateConfigEvent.class)) {
            _STATE=_UPDATE_CONFIG_STATE;
//            _FAR_WAYS.pause();
//
//            try {
//                String url = _CURRENT_CONFIG.optString("UpdateUrl");
//
//                if (url.compareTo("") != 0) {
//                    JSONObject cfg = obtainConfig(_ObjectID, url);
//
//                    if (cfg != null) {
//                        _CURRENT_CONFIG = cfg;
//                        cfg = null;
//
//                        _FAR_WAYS.clear();
//                        if (acceptConfig(_CURRENT_CONFIG, true)) {
//                            saveAkNumber(_ObjectID);
//                            AkMessage2 m = new AkMessage2();
//                            m.set_AkNumber(_ObjectID);
//                            m.set_EventClass(5);
//                            m.set_EventCod(3);
//                            m.set_ZoneNumber(0);
//                            m.set_PartitionNumber(0);
//                            m.set_MesNumber(get_MessageNumber());
//                            synchronized (_MESSAGES) {
//                                _MESSAGES.addElement(m);
//                                _MESSAGES.notifyAll();
//                            }
//                        }
//
//                    }
//                }
//            } catch (Exception ex) {
//            }
//
//            _FAR_WAYS.resume();
        } else if (event.getClass().equals(UpdateFirmwareEvent.class)) {
            //,"http://okotc.np-ip.org/updates/oko3c.jad",,,,"GPRS","inet.ycc.ru","motiv","motiv","208.67.222.222","http://okotc.np-ip.org/updates/aaa.php","off","off"
            //update();
            _STATE=_UPDATE_FIRMWARE_STATE;

        } else if (event.getClass().equals(EndCallEvent.class)) {
            StringBuffer bf = new StringBuffer();
            try {
//                synchronized (_ATC) 
                {

                    _ATC.send("AT^SM20=1\r");
                    _ATC.send("ATH\r", this);

                }
            } catch (IllegalStateException ex) {
            } catch (IllegalArgumentException ex) {
            }
//            if (bf.toString().indexOf("OK") >= 0) {
//                _CALL_STATE = 0;
//                _INDICATION.setLed(call_led, Indicator.OFF);
//                System.out.println("Terminate call");
//            }
        }

    }

    private int get_MessageNumber() {
        int res = 0;
        if (this._MESSAGE_NUBER >= 255) {
            this._MESSAGE_NUBER = 1;
        }
        res = this._MESSAGE_NUBER++;
        return res;
    }

    private void reset_MessageNumbers() {
        this._MESSAGE_NUBER = 1;
    }

    private void saveCID(String number) {
        FileConnection fc = null;
        DataOutputStream dos = null;

        try {

            fc = (FileConnection) Connector.open("file:///a:/storage/cid", Connector.READ_WRITE);
            if (!fc.exists()) {
                fc.create();
            } else {
                fc.delete();
                fc.create();
            }
            dos = fc.openDataOutputStream();
            dos.writeUTF(number);
        } catch (IOException ex) {
        }

        try {
            if (dos != null) {
                dos.close();
            }
            if (fc != null) {
                fc.close();
            }
        } catch (IOException ex) {
        }
    }

    private String loadCID() {
        String res = "";

        FileConnection fc = null;
        DataInputStream dis = null;
        try {
            fc = (FileConnection) Connector.open("file:///a:/storage/cid", Connector.READ);
            if (fc.exists()) {
                dis = fc.openDataInputStream();
                res = dis.readUTF();
            }
        } catch (IOException ex) {
        }

        try {
            if (dis != null) {
                dis.close();
            }
            if (fc != null) {
                fc.close();
            }
        } catch (IOException ex) {
        }
        return res;
    }

    private void saveMODE() {
        FileConnection fc = null;
        DataOutputStream dos = null;
        try {
            fc = (FileConnection) Connector.open("file:///a:/storage/mod", Connector.READ_WRITE);
            if (!fc.exists()) {
                fc.create();
            } else {
                fc.delete();
                fc.create();
            }
            dos = fc.openDataOutputStream();
            dos.writeChar((int) _MODE);
        } catch (IOException ex) {
        }

        try {
            if (dos != null) {
                dos.close();
            }
            if (fc != null) {
                fc.close();
            }
        } catch (IOException ex) {
        }
    }

    private char loadMODE() throws IOException {
        char res = (char) 0x0;

        FileConnection fc = null;
        DataInputStream dis = null;
        try {
            fc = (FileConnection) Connector.open("file:///a:/storage/mod", Connector.READ);
            if (fc.exists()) {
                dis = fc.openDataInputStream();
                res = dis.readChar();
            } else {
                throw new IOException("[Terminal] Terminal()->LoadMODE()->mod file not exist");
            }
        } catch (IOException ex) {
        } catch (Exception ex) {
        }

        try {
            if (dis != null) {
                dis.close();
            }
            if (fc != null) {
                fc.close();
            }
        } catch (IOException ex) {
        } catch (Exception ex) {
        }
        return res;
    }

//    public int reinitGsm() {
//        String resp = "";
//        int apmode = -1;
//        try {
//            this._RESTART_GSM = true;
//            this._FAR_WAYS.pause();
//            switch (_PLATFORM) {
//                case _TC65_PLATFORM:
//                    _SYSSTART=false;
//                    _PBREADY=false;
//                    resp = _ATC.send("AT^SCFG=\"MEopMode/Airplane\",\"on\"\r");//Включить режим в самолёте
//                    System.out.println("answer " + resp);              
//                    break;
//                default:
//                    _SYSSTART = false;
//                    _PBREADY = false;
//                    System.out.println("send AT+CFUN=4,0");
//                    resp = _ATC.send("AT+CFUN=4,0\r");//Включить режим в самолёте
//                    System.out.println("answer " + resp);
//                    break;
//            }
//
//            if (resp.toUpperCase().indexOf("ERR") >= 0) {
//
//                if (resp.indexOf("OK") >= 0) {
//                    apmode = 1;
//                }
//            } else if (resp.indexOf("OK") >= 0) {
//                apmode = 2;
//            }
//            if (apmode == 1) {
//                resp = _ATC.send("AT^SCFG=\"MEopMode/Airplane\",\"off\"\r");//включить полный режим
//                System.out.println("send AT+CFUN=1,0");
//                resp = _ATC.send("AT+CFUN=1,0\r");//включить полный режим
//                System.out.println("answer " + resp);
//                if (resp.indexOf("OK") >= 0) {//команда выполнена
//                    System.out.println("send AT+CFUN?");
//                    resp = _ATC.send("AT+CFUN?\r");//запросить текущий режим
//                    System.out.println("answer " + resp);
//                    if (resp.indexOf("+CFUN: 1") >= 0) {//проверить что режим полной функциональности
//                        System.out.println("send AT+COPS=0");
//                        resp = _ATC.send("AT+COPS=0\r");//включить автовыбор оператора.
//                        
//                        System.out.println("answer " + resp);
//                        if (resp.indexOf("OK") >= 0) {
//                            System.out.println("send AT+COPS?");
//                            resp = _ATC.send("AT+COPS?\r");
//                            System.out.println("answer " + resp);
//                            if (resp.indexOf("+COPS: 0") >= 0) {
//                                String cid = "";
////                                    try{
////                                    initAT();
////                                    }catch(IllegalArgumentException ex){
////                                        ex.printStackTrace();
////                                    } catch(IllegalStateException ex){
////                                        ex.printStackTrace();
////                                    }catch(ATCommandFailedException ex){
////                                        ex.printStackTrace();
////                                    }
//                                this._FAR_WAYS.fill();
//                                try {
//
//                                    cid = _MONITOR.getCID();
//                                } catch (IllegalStateException ex) {
//                                } catch (Exception ex) {
//                                } catch (Error ex) {
//                                }
//                                if (_CID.compareTo(cid) != 0) {
//                                    _CID = cid;
//                                    saveCID(_CID);
//                                    SMSMessage cidsms = new SMSMessage();
//                                    if (this._SPY_NUMBER.compareTo("") != 0) {
//                                        cidsms.Phone = this._SPY_NUMBER;
//                                        cidsms.Text = "9:001-301-" + _ObjectID;
//                                        synchronized (_MESSAGES) {
//                                            _MESSAGES.addElement(cidsms);
//                                            _MESSAGES.notifyAll();
//                                        }
//                                    }
//                                }
//                            } else {
//                                this._RESTART_GSM = false;
//                                return -1;
//
//                            }
//                        } else {
//                            this._RESTART_GSM = false;
//                            return -1;
//                        }
//                    } else {
//                        this._RESTART_GSM = false;
//                        return -1;
//                    }
//                } else {
//                    this._RESTART_GSM = false;
//                    return -1;
//                }
//            } else if (apmode == 2) {// команда выполнена
//                System.out.println("send AT+CFUN?");
//                resp = _ATC.send("AT+CFUN?\r");//Запросить текущий режим                    
//                System.out.println("answer " + resp);
//                if (resp.indexOf("+CFUN: 4,0") >= 0) {//проверить что режим - в самолёте
//                    System.out.println("send AT+CFUN=1,0");
//                    resp = _ATC.send("AT+CFUN=1,0\r");//включить полный режим
//                    System.out.println("answer " + resp);
//                    if (resp.indexOf("OK") >= 0) {//команда выполнена
//                        System.out.println("send AT+CFUN?");
//                        resp = _ATC.send("AT+CFUN?\r");//запросить текущий режим
//                        System.out.println("answer " + resp);
//                        if (resp.indexOf("+CFUN: 1") >= 0) {//проверить что режим полной функциональности
//                            System.out.println("send AT+COPS=0");
//                            resp = _ATC.send("AT+COPS=0\r");//включить автовыбор оператора.
//                            System.out.println("answer " + resp);
//                            if (resp.indexOf("OK") >= 0) {
//                                System.out.println("send AT+COPS?");
//                                resp = _ATC.send("AT+COPS?\r");
//                                System.out.println("answer " + resp);
//                                if (resp.indexOf("+COPS: 0") >= 0) {
//                                    String cid = "";
////                                    try{
////                                    initAT();
////                                    }catch(IllegalArgumentException ex){
////                                        ex.printStackTrace();
////                                    } catch(IllegalStateException ex){
////                                        ex.printStackTrace();
////                                    }catch(ATCommandFailedException ex){
////                                        ex.printStackTrace();
////                                    }
//                                    this._FAR_WAYS.fill();
//                                    try {
//
//                                        cid = _MONITOR.getCID();
//                                    } catch (IllegalStateException ex) {
//                                    } catch (Exception ex) {
//                                    } catch (Error ex) {
//                                    }
//                                    if (_CID.compareTo(cid) != 0) {
//                                        _CID = cid;
//                                        saveCID(_CID);
//                                        SMSMessage cidsms = new SMSMessage();
//                                        if (this._SPY_NUMBER.compareTo("") != 0) {
//                                            cidsms.Phone = this._SPY_NUMBER;
//                                            cidsms.Text = "9:001-301-" + _ObjectID;
//                                            synchronized (_MESSAGES) {
//                                                _MESSAGES.addElement(cidsms);
//                                                _MESSAGES.notifyAll();
//                                            }
//                                        }
//                                    }
//                                } else {
//                                    this._RESTART_GSM = false;
//                                    return -1;
//
//                                }
//                            } else {
//                                this._RESTART_GSM = false;
//                                return -1;
//                            }
//                        } else {
//                            this._RESTART_GSM = false;
//                            return -1;
//                        }
//                    } else {
//                        this._RESTART_GSM = false;
//                        return -1;
//                    }
//                } else {
//                    this._RESTART_GSM = false;
//                    return -1;
//                }
//            } else {
//                this._RESTART_GSM = false;
//                return -1;// ATEvent("+CIEV: service,0");
//            }
//
//        } catch (ATCommandFailedException ex) {
//            return -1;
//        } catch (IllegalStateException ex) {
//            return -1;
//        } catch (IllegalArgumentException ex) {
//            return -1;
//        }
//        this._RESTART_GSM = false;
//        return 0;
//    }
//   public String getCID() throws ATCommandFailedException, IllegalStateException, Exception, Error {
//        String answer = "";
//        try {
//            if(this._ATC==null){
//                System.out.println("ATCommand in gsmMon is null!!!");
//                return "";
//            }
//           /*synchronized (_ATC)*/
//           { 
//            switch(_PLATFORM){
//                case(_BGS5_PLATFORM):
//                    _ATC.send("AT+CCID\r",this);
//                    break;
//                case (_EHS5_PLATFORM):
//                    _ATC.send("AT+CCID\r",this);
//                    break;
//                case (_TC65_PLATFORM):
//                    _ATC.send("AT^SCID\r",this);
//                    break;
//                    
//            }
//
//            }
//            answer = answer.trim();
//            answer = (answer.substring(answer.indexOf(':')+1, answer.indexOf('\r'))).trim();
//        } catch (IllegalStateException ex) {
//            ex.printStackTrace();
//            throw new ATCommandFailedException(ex.getMessage() + "->gsm.gsmMon.getCID()");
//        } catch (Exception ex) {
//            throw new Exception(ex.getMessage() + "->gsm.gsmMon.getCID()");
//        } catch (Error err) {
//            throw new Error(err.getMessage() + "->gsm.gsmMon.getCID()");
//        }
//        System.out.println(answer);
//        return answer;
//    }
    private void saveAkNumber(int number) {
        FileConnection fc = null;
        DataOutputStream dos = null;
        try {
            fc = (FileConnection) Connector.open("file:///a:/storage/aknumber", Connector.READ_WRITE);
            if (!fc.exists()) {
                fc.create();
            }
            dos = fc.openDataOutputStream();
            dos.writeInt(number);
        } catch (IOException ex) {
        }

        try {
            if (dos != null) {
                dos.close();
            }
            if (fc != null) {
                fc.close();
            }
        } catch (IOException ex) {
        }
    }

    /**
     *
     */
    public void enable_Audio() {
        try {
            System.out.println("[Terminal] Enable_Audio()");
            //#ifdef BGS5Autostart
//#             _AUDIO.setValue(0);
            //#elifdef BGS5Debug
//#                         _AUDIO.setValue(0);
            //#elifdef BGS5AutostartOldInd
//#                         _AUDIO.setValue(0);
            //#elifdef BGS5DebugOldInd
//#                         _AUDIO.setValue(0);            
            //#elifdef EHS5Debug
//#                     _AUDIO.setValue(0);
            //#elifdef EHS5DebugOldInd
//#                     _AUDIO.setValue(0);
            //#elifdef EHS5Autostart
//#                     _AUDIO.setValue(0);
            //#elifdef EHS5AutostartOldInd
//#             _AUDIO.setValue(0);
            //#endif
        } catch (Exception ex) {
        }
    }

    /**
     *
     */
    public void disable_Audio() {
        try {
            System.out.println("[Terminal] Disable_Audio()");
            //#ifdef BGS5Autostart
//#             _AUDIO.setValue(1);
            //#elifdef BGS5Debug
//#                         _AUDIO.setValue(1);
            //#elifdef EHS5Debug
//#                     _AUDIO.setValue(1);
            //#elifdef EHS5DebugOldInd
//#                     _AUDIO.setValue(1);
            //#elifdef BGS5AutostartOldInd
//#                     _AUDIO.setValue(1);
            //#elifdef BGS5DebugOldInd
//#                     _AUDIO.setValue(1);            
            //#elifdef EHS5Autostart
//#                     _AUDIO.setValue(1);
            //#elifdef EHS5AutostartOldInd
//#             _AUDIO.setValue(1);
            //#endif
        } catch (Exception ex) {
        }
    }

    /**
     *
     */
    public void clearFS() {
        System.out.print("[Terminal] ClearFS()");
        try {
            //
            FileConnection fcjar = (FileConnection) Connector.open("file:///a:/TC65X3.jar", Connector.WRITE);
            fcjar.delete();
            fcjar.close();
        } catch (Exception ex) {
        } catch (Error err) {
        }
        try {

            FileConnection fcjad = (FileConnection) Connector.open("file:///a:/TC65X3.jad", Connector.READ_WRITE);
            if (fcjad.exists()) {
                fcjad.delete();
            }
            fcjad.close();
        } catch (Exception ex) {
        } catch (Error err) {
        }
        try {
            //
            FileConnection fcjar = (FileConnection) Connector.open("file:///a:/TC23C.jar", Connector.WRITE);
            fcjar.delete();
            fcjar.close();
        } catch (Exception ex) {
        } catch (Error err) {
        }
        try {

            FileConnection fcjad = (FileConnection) Connector.open("file:///a:/TC23C.jad", Connector.READ_WRITE);
            if (fcjad.exists()) {
                fcjad.delete();
            }
            fcjad.close();
        } catch (Exception ex) {
        } catch (Error err) {
        }

        try {

            FileConnection fcjad = (FileConnection) Connector.open("file:///a:/storage/TC65.ini", Connector.READ_WRITE);
            if (fcjad.exists()) {
                fcjad.delete();
                Util.deleteFolder("a:/storage/Logs");
            }
            fcjad.close();
        } catch (Exception ex) {
        } catch (Error err) {
        }

        try {

            Util.deleteFolder("a:/dist/");
        } catch (IOException ex) {
        }
        
        try {

            FileConnection fcjad = (FileConnection) Connector.open("file:///a:/storage/start.jad", Connector.READ_WRITE);
            if (!fcjad.exists()) {
                fcjad.create();
                _FIRST_START = true;
            }
            fcjad.close();
        } catch (Exception ex) {
        } catch (Error err) {
        }        
        
        System.out.println("[Terminal] END of ClearFS()");
    }

    /**
     *
     */
    public void prepareAudio() {
        Vector pins = new Vector(1);
        Vector values = new Vector(1);
        pins.addElement("GPIO8");
        if (_AUDIO != null) {
            return;
        }
        //#ifdef EHS5AutostartOldInd
//#         values.addElement(Integer.valueOf("1"));
//#         try {
//#             _AUDIO = new OutPort(pins, values);
//#         } catch (IOException ex) {
//#             ex.printStackTrace();
//#         }
        //#elifdef EHS5DebugOldInd
//#             values.addElement(Integer.valueOf("1"));
//#             try{
//#             _AUDIO=new OutPort(pins,values);
//#             }catch(IOException ex){
//#                 ex.printStackTrace();
//#             }
        //#elifdef BGS5AutostartOldInd
//#         values.addElement(Integer.valueOf("1"));
//#         try {
//#             _AUDIO = new OutPort(pins, values);
//#         } catch (IOException ex) {
//#             ex.printStackTrace();
//#         }
        //#elifdef BGS5DebugOldInd
//#             values.addElement(Integer.valueOf("1"));
//#             try{
//#             _AUDIO=new OutPort(pins,values);
//#             }catch(IOException ex){
//#                 ex.printStackTrace();
//#             }        
        //#elifdef TC65Gpio
//#             System.out.println("PrepareAudio():TC65Gpio: Nothing to do");
        //#elifdef BGS5Debug
//# 
//#             values.addElement(Integer.valueOf("1"));
//#             try {
//#                 _AUDIO = new OutPort(pins, values);
//#             } catch (IOException ex) {
//#                 ex.printStackTrace();
//#             }
        //#elifdef BGS5Autostart
//#         values.addElement(Integer.valueOf("1"));
//#         try {
//#             _AUDIO = new OutPort(pins, values);
//#         } catch (IOException ex) {
//#             ex.printStackTrace();
//#         }
//# 
        //#elifdef EHS5Autostart
//#             values.addElement(Integer.valueOf("1"));
//#             try{
//#             _AUDIO = new OutPort(pins, values);               
//#             }catch(IOException ex){
//#                 ex.printStackTrace();
//#             }
        //#elifdef EHS5Debug
//#             values.addElement(Integer.valueOf("1"));
//#             try{
//#             _AUDIO = new OutPort(pins, values);                           
//#             }catch(IOException ex){
//#                 ex.printStackTrace();
//#             }
        //#endif        
    }

    private int createConfig() {
        System.out.println("[Terminal] CreateConfig()");
        if ((_CURRENT_CONFIG = loadConfig()) == null) {
            _CURRENT_CONFIG = loadDefaultConfig();
            System.out.println("[Terminal] Load default config!!!!");
        } else {
            System.out.println("[Terminal] Config loaded!!!!");
        }
        JSONObject syst = _CURRENT_CONFIG.optJSONObject("System");
        int objid = syst.optInt("objectID", 0);
        System.out.println("[Terminal] Loaded object number: " + objid);
        acceptConfig(_CURRENT_CONFIG, true);
        return objid;
    }

    private void setJavaNetSettings() {
        System.out.println("SetJavaNetSettings()");
        getCurState();
//        Vector cstate = getCurState();
//        Enumeration en = cstate.elements();
//        String tok = "";
//        while (en.hasMoreElements()) {
//            tok = (String) en.nextElement();
//            System.out.println(tok);
//            if (tok == null) {
//                continue;
//            }
//            if (tok.equals("")) {
//                continue;
//            }
//            if (tok.indexOf("eons") >= 0) {
//                String[] tk = StringPraser.split(tok);
//
//                //^SIND: eons,0,5,"CC 250 NC 35","MOTIV"
//                for (int i = 0; i < tk.length; i++) {
//                    System.out.println("tk lenght: " + tk.length);
//                    System.out.println("[" + i + "]: " + tk[i]);
//                }
//
//                if (tk.length >= 4) {
//                    if (tk[4].trim().length() > 0) {
//                        if (tk[4].trim().compareTo("") == 0 || tk[4].trim().compareTo("\"\"") == 0) {
//                            try {
//                                set_JavaTCPDefault();
//                            } catch (Exception ex) {
//                            }
//                        } else {
//                            set_JavaTCPbyName(tk[4].trim());
//                            _CURRENT_OPERATOR_NAME=tk[4];
//                        }
//                    } else {
//                        try {
//                            set_JavaTCPDefault();
//                        } catch (IllegalStateException ex) {
//                        } catch (Exception ex) {
//                        } catch (Error ex) {
//                        }
//                    }
//                } else {
//                    try {
//                        set_JavaTCPDefault();
//                    } catch (IllegalStateException ex) {
//                    } catch (Exception ex) {
//                    } catch (Error ex) {
//                    }
//                }
//                break;
//            }
//        }
//        System.out.println("END of SetJavaNetSettings()");
    }

    private void beep() {
        //#ifdef EHS
//#             try {
//#                 Manager.playTone(0x45, 1000, 100);
//#             }
//#             catch (MediaException me) {
//#                 me.printStackTrace();
//#             }
//#             catch (Exception ex) {
//#                 ex.printStackTrace();
//#             }
//#             catch (Error err) {
//#                 err.printStackTrace();
//#             }
        //#endif
    }

    private boolean check_Net_Mode() {
        boolean res = true;
        //#ifdef EHS            
//#         String resp = "";
//# 
//#         try {
//#             _ATC.send("AT^SMONI\r",this);
//#         } catch (IllegalStateException ex) {
//#             ex.printStackTrace();
//#             res = false;
//#         } catch (IllegalArgumentException ex) {
//#             ex.printStackTrace();
//#             res = false;
//#         }
//# 
//#         if (resp.indexOf("2G") >= 0) {
//#             if (_NET_MODE != CommunicationManager._NET_MODE_2G) {
//#                 _NET_MODE = CommunicationManager._NET_MODE_2G;
//#             }
//#         } else if (resp.indexOf("3G") >= 0) {
//#             if (_NET_MODE != CommunicationManager._NET_MODE_3G) {
//#                 _NET_MODE = CommunicationManager._NET_MODE_3G;
//#             }
//#         } else if (resp.indexOf("4G") >= 0) {
//#             if (_NET_MODE != CommunicationManager._NET_MODE_4G) {
//#                 _NET_MODE = CommunicationManager._NET_MODE_4G;
//#             }
//#         } else {
//#             if (_NET_MODE != CommunicationManager._NET_MODE_2G) {
//#                 _NET_MODE = CommunicationManager._NET_MODE_2G;
//#             }
//#         }
        //#else

        if (_NET_MODE != CommunicationManager._NET_MODE_2G) {
            _NET_MODE = CommunicationManager._NET_MODE_2G;
        }
        //#endif
        return res;
    }

    private void check_AirPlane_Mode() {

        String response = "";
        System.out.println("[Terminal] Check Air Plane Mode");
        switch (_PLATFORM) {
            case _TC65_PLATFORM: {
                try {
                    _ATC.send("AT^SCFG=\"MEopMode/Airplane\"\r", this);
//                    if(response.indexOf("on")>=0){
//                        return true;
//                    }
                } catch (IllegalStateException ex) {
                    ex.printStackTrace();
                } catch (IllegalArgumentException ex) {
                    ex.printStackTrace();
                }
            }
            break;
            default: {
                try {
                    _ATC.send("AT+CFUN?\r", this);
//                    if (response.indexOf("+CFUN: 4") >= 0) {
//                        return true;
//                    }
                } catch (IllegalStateException ex) {
                    ex.printStackTrace();
                } catch (IllegalArgumentException ex) {
                    ex.printStackTrace();
                }
            }
            break;

        }

//        return false;
    }

    private boolean enable_AirPlane_Mode() {
        String resp = "";

        if (_AIRPLANE_MODE) {
            return true;
        }
        System.out.println("[Terminal] Enable Air Plane Mode");
        try {
            this._RESTART_GSM = true;
            this._FAR_WAYS.pause();
            switch (_PLATFORM) {
                case _TC65_PLATFORM:
                    _SYSSTART = false;
                    _PBREADY = false;
                    _ATC.send("AT^SCFG=\"MEopMode/Airplane\",\"on\"\r", this);//Включить режим в самолёте
                    System.out.println("answer " + resp);
                    break;
                default:
                    _SYSSTART = false;
                    _PBREADY = false;
                    System.out.println("send AT+CFUN=4,0");
                    _ATC.send("AT+CFUN=4,0\r", this);//Включить режим в самолёте
                    System.out.println("answer " + resp);
                    break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    private void disable_AirPlane_Mode() {
        String resp = "";
        System.out.println("[Terminal] Disable Air Plane Mode");
        try {
            this._RESTART_GSM = true;
            this._FAR_WAYS.pause();
            _SYSSTART = false;
            _PBREADY = false;
            switch (_PLATFORM) {
                case _TC65_PLATFORM:

                    _ATC.send("AT^SCFG=\"MEopMode/Airplane\",\"off\"\r", this);//Выключить режим в самолёте                    
                    break;
                default:
                    System.out.println("[Terminal] send AT+CFUN=1,0");
                    _ATC.send("AT+CFUN=1,0\r", this);//Выключить режим в самолёте
                    break;
            }
            _ATC.send("AT+COPS=0\r", this);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    /**
     *
     * @param Response
     */
    public void ATResponse(String Response) {
        
        System.out.println("[Terminal] ATResponse(String Response): \r\n" + Response + "\r\n");
        //#ifdef TC65SpiDebug
//#         if(!_SYSSTART && !_PBREADY)
//#             if(Response.trim().compareTo("ATOK")==0){
//#                 _SYSSTART=true;
//#                 _PBREADY = true;
//#             }
//#         
        //#endif        

        //#ifdef TC65SpiConceptDebug
//# 
//#         if(!_SYSSTART && !_PBREADY)
//#             if(Response.trim().compareTo("OK")==0){
//#                 _SYSSTART=true;
//#                 _PBREADY = true;
//#             }
        //#endif
        
        
        //#ifdef TC65SpiConcept
//#         if(!_SYSSTART && !_PBREADY)
//#             if(Response.trim().compareTo("ATOK")==0){
//#                 _SYSSTART=true;
//#                 _PBREADY = true;
//#             }
        //#endif        
        
        //#ifdef TC65GpioDebug
//#         if(!_SYSSTART && !_PBREADY)
//#             if(Response.trim().compareTo("ATOK")==0){
//#                 _SYSSTART=true;
//#                 _PBREADY = true;
//#             }
        //#endif        

        //#ifdef TC65Gpio
//#         if(!_SYSSTART && !_PBREADY)
//#             if(Response.trim().compareTo("ATOK")==0){
//#                 _SYSSTART=true;
//#                 _PBREADY = true;
//#             }
        //#endif        
        
//#ifdef TC65Spi
//# 
//#         if (Response.trim().compareTo("ATOK") == 0) {
//#             if (!_SYSSTART && !_PBREADY) {
//#                 System.out.println("Set _SYSSTART and _PBREADY to true");
//#                 _SYSSTART = true;
//#                 _PBREADY = true;
//#             }
//#         }
        //#endif
        
        if (Response.indexOf("SIND") > -1) {
            parseSIND(Response);
        } else if (Response.toUpperCase().indexOf("TC65") >= 0) {
            _PLATFORM = _TC65_PLATFORM;
            return;
        } else if (Response.toString().toUpperCase().indexOf("BGS5") >= 0) {
            _PLATFORM = _BGS5_PLATFORM;
            return;
        } else if (Response.toUpperCase().indexOf("EHS5") >= 0) {
            _PLATFORM = _EHS5_PLATFORM;
            return;
        } else if (Response.toUpperCase().indexOf("BGSX") >= 0) {
            _PLATFORM = _BGS5_PLATFORM;
            return;
        } else if (Response.toUpperCase().indexOf("EHSX") >= 0) {
            _PLATFORM = _EHS5_PLATFORM;
            return;
        } else if (Response.indexOf("AT+CPBR=1") >= 0 || Response.indexOf("+CPBR: 1") >= 0) {
            String[] tokens = StringPraser.split(Response);
            if (tokens.length >= 5) {
                tokens[2] = tokens[2].replace('"', ' ');
                tokens[2] = tokens[2].trim();
//                if (_CALL_TO_LIST.elementAt(0) != null) {
//                    _CALL_TO_LIST.removeElementAt(0);
//                    _CALL_TO_LIST.insertElementAt(null, 0);
//                }
                _CALL_TO_LIST.setElementAt(tokens[2], 0);
            }

        } else if (Response.indexOf("AT+CPBR=2") >= 0 || Response.indexOf("+CPBR: 2") >= 0) {
            String[] tokens = StringPraser.split(Response);
            if (tokens.length >= 5) {
                tokens[2] = tokens[2].replace('"', ' ');
                tokens[2] = tokens[2].trim();
//                if (_CALL_TO_LIST.elementAt(1) != null) {
//                    _CALL_TO_LIST.removeElementAt(1);
//                }
                _CALL_TO_LIST.setElementAt(tokens[2], 1);
            }
        } else if (Response.indexOf("AT+CPBR=3") >= 0 || Response.indexOf("+CPBR: 3") >= 0) {
            String[] tokens = StringPraser.split(Response);
            if (tokens.length >= 5) {
                tokens[2] = tokens[2].replace('"', ' ');
                tokens[2] = tokens[2].trim();
//                if (_CALL_TO_LIST.elementAt(2) != null) {
//                    _CALL_TO_LIST.removeElementAt(2);
//                }
                _CALL_TO_LIST.setElementAt(tokens[2], 2);
            }
        } else if (Response.indexOf("+COPS:") >= 0) {
            String[] tokens = StringPraser.split(Response);

            if (tokens.length >= 3) {

                if (tokens[2].compareTo("0") == 0) {
                    tokens[3] = tokens[3].replace('"', ' ');
                    tokens[3] = tokens[3].trim();
                    if (tokens[3].compareTo("") != 0) {
                        _CURRENT_OPERATOR_NAME = tokens[3];
                    }
                } else if (tokens[2].compareTo("2") == 0) {
                    tokens[3] = tokens[3].replace('"', ' ');
                    tokens[3] = tokens[3].trim();
                    if (tokens[3].compareTo("") != 0) {
                        try {
                            _CURRENT_OPERATOR_ID = Integer.parseInt(tokens[3]);
                        } catch (Exception ex) {

                        }
                    }
                }
            }
        } else if (Response.indexOf("ATD>SM") >= 0 && Response.indexOf("OK") >= 0) {
            _CALL_STATE = 1;
        } else if (Response.indexOf("+CLCC") >= 0) {
            Call cl = parse_Call_Info(Response);

//            Call cl = get_Call();
//            if (cl == null) {
//                return;
//            }
//
//            if (_LAST_CALL != null) {
//                if (cl.NUMBER.compareTo(_LAST_CALL.NUMBER) == 0 && cl.DIRECTION == _LAST_CALL.DIRECTION) {
//                    cl.EXTENDED_INDEX = _LAST_CALL.EXTENDED_INDEX;
//                }
//                _LAST_CALL = cl;
//            } else if (cl.DIRECTION == 1) {
//                set_Call(cl);
//            }            
            if (cl == null) {
                System.out.println("[Terminal] Can`t parse call info!");
                return;
            }//Разобраться чего тут вобще происходит.

            System.out.println("[Terminal] Try to handle call " + cl.toString());

            if (_LAST_CALL != null) {
                System.out.println("[Terminal] _LAST_CALL != null");
                _LAST_CALL.EXTENDED_INDEX = 1;
                if (cl.DIRECTION == 1 && cl.STATE != 0) {//входящий звонок
                    if (_RESTART_BY_CALL_LIST.indexOf(cl.NUMBER) > -1) {
                        System.out.println("[Terminal] Try to restart by call");
                        _STATE = _RESTART_BY_CALL_STATE;
                        return;
                    }

                    if (check_call(cl)) {

                        set_Call(cl);
                        answer_on_call(cl);

                    } else {
                        hungup_call(cl, (char) 0x21);
                    }
                    return;
                } else if (cl.DIRECTION == 0 && cl.STATE != 0) {//исходящий звонок
                    set_Call(cl);
                }
            } else {
                System.out.println("[Terminal] _LAST_CALL is null");
                if (cl.DIRECTION == 1 && cl.STATE != 0) {//
                    if (_RESTART_BY_CALL_LIST.contains(cl.NUMBER)) {
                        _STATE = _RESTART_BY_CALL_STATE;
                    }
                }
                set_Call(cl);
            }
        } else if (Response.indexOf("ATH") >= 0 && Response.indexOf("OK") >= 0) {
            _CALL_STATE = 0;
            _INDICATION.setLed(call_led, Indicator.OFF);
        } else if (Response.indexOf("AT^SCFG=\"MEopMode/Airplane\"") > -1) {
            if (Response.indexOf("on") > -1) {
                if (_AIRPLANE_MODE) {
                    return;
                }
                _AIRPLANE_MODE = true;
                if (_STATE != _RESTART_GSM_STATE) {
                    _PREV_STATE = _STATE;
                    _STATE = _RESTART_GSM_STATE;
                }

                return;
            } else {
                _AIRPLANE_MODE = false;
                return;
            }
        } else if (Response.toUpperCase().indexOf("AT+CFUN=4") > -1 && Response.toUpperCase().indexOf("OK") > -1) {
            
            if (!_AIRPLANE_MODE) {
                _AIRPLANE_MODE = true;
            }
            _AIRPLANE_CHANGED = true;
        } else if (Response.toUpperCase().indexOf("AT+CFUN=1") > -1 && Response.toUpperCase().indexOf("OK") > -1) {
            if (_AIRPLANE_MODE) {
                _AIRPLANE_MODE = false;
                
            }
            _AIRPLANE_CHANGED = true;
        } else if (Response.indexOf("AT+CFUN?") > -1) {
            _AIRPLANE_CHANGED = true;
            switch (_PLATFORM) {
                case (_TC65_PLATFORM):
                    break;
                default:

                    if (Response.indexOf("+CFUN: 4") >= 0) {
                        synchronized(_TERM_STATE){
                            _TERM_STATE.FUNC=TerminalState.FUNC_AIRPLANE;
                            _TERM_STATE.notifyAll();
                        }
                        if (_AIRPLANE_MODE) {
                            return;
                        }
                        _AIRPLANE_MODE = true;
                        if (_STATE != _RESTART_GSM_STATE) {                          
                            _STATE = _RESTART_GSM_STATE;
                        }

                    } else {
                        _AIRPLANE_MODE = false;
                        synchronized (_TERM_STATE) {
                            _TERM_STATE.FUNC = TerminalState.FUNC_FULL;
                            _TERM_STATE.notifyAll();
                        }

                    }

                    break;
            }

        } else if (Response.indexOf("END AT INIT") > -1) {
            System.out.println("_PREV_STATE: "+(int)_PREV_STATE+" _STATE: "+(int)_STATE);
            if(_PREV_STATE == _START_STATE && _STATE==_INIT_AT_STATE){
                _STATE = _INIT_STATE;
            }
        } else if (Response.indexOf("END INIT") > -1) {
            System.out.println("_PREV_STATE: "+(int)_PREV_STATE+" _STATE: "+(int)_STATE);
            if (_PREV_STATE == _INIT_AT_STATE && _STATE==_INIT_STATE) {
                _STATE = _WORK_STATE;
            }
        } else if (Response.indexOf("AT^SJNET=") > -1) {
            if (Response.indexOf("OK") > -1) {
                _JAVA_NET = true;
                if(Response.toUpperCase().indexOf("MOTIV")>-1){
                    _JAVA_NET_NAME = "MOTIV";
                }else if(Response.toUpperCase().indexOf("MTS")>-1){
                    _JAVA_NET_NAME = "MTS";
                }else if(Response.toUpperCase().indexOf("BEELINE")>-1){
                    _JAVA_NET_NAME = "BEELINE";
                }else if(Response.toUpperCase().indexOf("MEGAFON")>-1){
                    _JAVA_NET_NAME = "MEGAFON";
                }else if(Response.toUpperCase().indexOf("TELE2")>-1){
                    _JAVA_NET_NAME = "TELE2";
                }else if(Response.toUpperCase().indexOf("RUSEC")>-1){
                    _JAVA_NET_NAME = "RUSEC";
                }else if(Response.toUpperCase().indexOf("USI")>-1){
                    _JAVA_NET_NAME = "USI";
                }else{
                    _JAVA_NET_NAME = "UNDEFINED";
                }
            } else if (Response.indexOf("ERROR") > -1) {
                _JAVA_NET = false;
               
            }
        } else if ((Response.indexOf("+CCID") > -1 || Response.indexOf("^SCID") > -1) && Response.indexOf("OK") > -1) {
            String[] tokens = StringPraser.split(Response);
            if (tokens.length > 1) {
                _CID = tokens[1].trim();
            }
        } else if ((Response.indexOf("AT^SJOTAP") > -1) && (Response.indexOf("ERROR")) > -1) {
            _FAR_WAYS.resume();
            _STATE = _WORK_STATE;
        }
    }

    private void parseSIND(String data) {
        System.out.println("[Terminal] Parse SIND data");
        if (data.indexOf("AT^SIND?") > -1) {
            data = data.substring(data.indexOf("AT^SIND?") + "AT^SIND?".length(), data.length()).trim();
//            System.out.println("DATA:\r\n"+data);
        }
        Vector t = StringPraser.getDelimSeparatedValues(data, '\r');
        String[] tokens = new String[t.size()];
        t.copyInto(tokens);
        for (int i = 0; i < tokens.length; i++) {
//            System.out.println("token[" + i + "]: " + tokens[i].trim());
            if (tokens[i] != null) {
                if (tokens[i].indexOf("^SIND:") > -1) {
                    tokens[i] = tokens[i].substring(tokens[i].indexOf("^SIND:") + "^SIND:".length(), tokens[i].length()).trim();
//                    System.out.println("token[" + i + "]: " + tokens[i].trim());
                    String[] values = StringPraser.split(tokens[i]);
                    if (values.length > 2) {

                        if (values[0].trim().compareTo("rssi") == 0) {
                            System.out.println("[Terminal] rssi found");
                            try {

                                _SIGNAL_LEVEL = (Integer.parseInt(values[2].trim())) * 20;
                                System.out.println("[Terminal] Set signal level: " + _SIGNAL_LEVEL);
                            } catch (Exception ex) {

                            }
                        } else if (values[0].trim().compareTo("eons") == 0) {
                            System.out.println("[Terminal] eons");
                            System.out.println("[Terminal] values.length: "+values.length);
                            {
                                for (int p = 0; p < values.length; p++) {
                                    System.out.println("[Terminal] values[" + p + "]: " + values[p]);
                                }
                            }
                            if (values.length > 3) {
                                if (values[3].compareTo("") != 0) {
                                    if (_CURRENT_OPERATOR_NAME.compareTo(values[3])!=0) {
                                        _CURRENT_OPERATOR_NAME = values[3];
                                        set_JavaTCPbyName(values[3].trim().toUpperCase());
                                    }
                                }
                            }
                        }
                    }
                }
            }

        }
////                if (Response.indexOf("rssi") >= 0) {
////            int rssi = 0;
////            if (Response.indexOf("OK") >= 0) {
////                Response = Response.substring(0, Response.indexOf("OK")).trim();
////            }
////            System.out.println("Response corrected:" + Response);
////            String[] Parse = Parser.Parse(Response.trim(), ',');
////            if (Parse.length == 3) {
////                try {
////                    rssi = Integer.parseInt(Parse[2].trim());
////                } catch (Exception ex) {
////
////                }
////            } else if (Parse.length == 2) {
////                try {
////                    rssi = Integer.parseInt(Parse[1].trim());
////                } catch (Exception ex) {
////
////                }
////            }
////            _SIGNAL_LEVEL = rssi * 20;
////            return;
////        }
    }

    private void get_Platform() {

//       String resp = "";
//            try {
        //Проверка типа платформы
        if (_PLATFORM == _PLATFORM_UNDEFINED) {
            _ATC.send("ATI\r", (ATCommandResponseListener) this);
        }

//            } catch (ATCommandFailedException ex) {
//                ex.printStackTrace();
//            } catch (IllegalStateException ex) {
//                ex.printStackTrace();
//            } catch (IllegalArgumentException ex) {
//                ex.printStackTrace();
//            }
//            if (resp.indexOf("TC65") >= 0) {
//                res = _TC65_PLATFORM;
//            } else if (resp.toString().indexOf("BGS5") >= 0) {
//                res = _BGS5_PLATFORM;
//            } else if (resp.toString().indexOf("EHS5") >= 0) {
//                res = _EHS5_PLATFORM;
//            } 
    }

    private void startState() {
        
        if (_PBREADY || _SYSSTART) {
            _STATE = _INIT_AT_STATE;
        } else {
            if (!send_once) {
                _ATC.send("AT\r", this);
                _ATC.send("ATI\r", this);
                _ATC.send("AT+IPR=115200\r", this);
                _ATC.send("AT+CFUN=1,0\r", this);
                send_once = true;
                Task t = new Task(this, new SysStartEvent(), "SYSSTART");
                if (_START_TIMER == null) {
                    _START_TIMER = new Timer();
                }else{
                    _START_TIMER.cancel();
                }
                _START_TIMER.schedule(t, 1000*10);
                
            }

            
            //#ifdef TC65Spi
//#             _ATC.send("AT\r", this);
//#             _ATC.send("ATI\r", this);
//#             _ATC.send("AT+IPR=115200\r", this);
//#             _ATC.send("AT+CFUN=1,0\r", this);
            //#endif
            //#ifdef TC65SpiDebug
//#                _ATC.send("AT\r", this);
//#                _ATC.send("ATI\r", this);
//#                _ATC.send("AT+IPR=115200\r", this);
//#                _ATC.send("AT+CFUN=1,0\r", this);
            //#endif
            //#ifdef TC65SpiConcept
//#                _ATC.send("AT\r", this);
//#                _ATC.send("ATI\r", this);
//#                _ATC.send("AT+IPR=115200\r", this);
//#                _ATC.send("AT+CFUN=1,0\r", this);
            //#endif
            //#ifdef TC65SpiConceptDebug
//#                _ATC.send("AT\r", this);
//#                _ATC.send("ATI\r", this);
//#                _ATC.send("AT+IPR=115200\r", this);
//#                _ATC.send("AT+CFUN=1,0\r", this);
            //#endif
            //#ifdef TC65Gpio
//#                _ATC.send("AT\r", this);
//#                _ATC.send("ATI\r", this);
//#                _ATC.send("AT+IPR=115200\r", this);
//#                _ATC.send("AT+CFUN=1,0\r", this);
            //#endif
            //#ifdef TC65GpioDebug
//#                _ATC.send("AT\r", this);
//#                _ATC.send("ATI\r", this);
//#                _ATC.send("AT+IPR=115200\r", this);
//#                _ATC.send("AT+CFUN=1,0\r", this);
            //#endif   

            synchronized (_MUTEX) {
                try {
                    _MUTEX.wait(3000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                _MUTEX.notifyAll();
            }
        }
    }

    private void initATState() {
        initAT();
        set_JavaTCPDefault();
        for (int i = 1; i < 4; i++) {
            _ATC.send("AT+CPBR=" + i + "\r", this);
        }
        get_Platform();
        synchronized (_MUTEX) {
            try {
                _MUTEX.wait(1000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            _MUTEX.notifyAll();
        }
        getCID();
        synchronized (_MUTEX) {
            try {
                _MUTEX.wait(1000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            _MUTEX.notifyAll();
        }
        getCurState();
        synchronized (_MUTEX) {
            try {
                _MUTEX.wait(1000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            _MUTEX.notifyAll();
        }
        System.out.println("Terminal.initATState(): send END AT INIT action");
        _ATC.send(new ATCmdEndAction("END AT INIT", this));
    }

    private void initState() {
        fill();            
        ///check_AirPlane_Mode();
    }

    private void restartGSMState() {
        if (_AIRPLANE_MODE_COUNTER == 4) {
            _STATE = _RESTART_STATE;
            return;
        }
        boolean oldairplane = _AIRPLANE_MODE;
        System.out.println("restartGSMState(): check_AirPlane_Mode()");
        check_AirPlane_Mode();        
        System.out.println("restartGSMState(): waite rsault of check");
        for (int i = 0; i < 600; i++) {
            updateTimeCounter();
            synchronized (_MUTEX) {
                try {
                    _MUTEX.wait(100);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                _MUTEX.notifyAll();
            } 
            if(_AIRPLANE_CHANGED){
                System.out.println("restartGSMState(): Airplane mode flag changed");
                _AIRPLANE_CHANGED = false;
                break;
            }
            
        }
        System.out.println("restartGSMState(): enable airplaine if needed");
        if (!_AIRPLANE_MODE) {
            enable_AirPlane_Mode();
            for (int i = 0; i < 600; i++) {
                synchronized (_MUTEX) {
                    try {
                        _MUTEX.wait(100);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    _MUTEX.notifyAll();
                }
                if(_AIRPLANE_MODE){
                    System.out.println("restartGSMState(): airplane mode is enabled!");
                    break;
                }
            }
//            check_AirPlane_Mode();
        } else {
            System.out.println("restartGSMState(): Try to disable Airplane Mode");
            disable_AirPlane_Mode();
            for (int i = 0; i < 600; i++) {
                updateTimeCounter();
                synchronized (_MUTEX) {
                    try {
                        _MUTEX.wait(100);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    _MUTEX.notifyAll();
                }
                if (!_AIRPLANE_MODE) {
                    _ATC.send("AT+COPS=0\r", this);
                    _ATC.send("AT+CGATT=1\r", this);
                    for (int k = 0; k < 6000; k++) {
                        updateTimeCounter();
                        synchronized (_MUTEX) {
                            try {
                                _MUTEX.wait(100);
                            } catch (InterruptedException ex) {
                                ex.printStackTrace();
                            }
                            _MUTEX.notifyAll();
                        }
                        if (_SERVICE == 1) {
                            System.out.println("SERVICE=1");
                            break;
                        }
                    }
                    _STATE = _WORK_STATE;
                    _AIRPLANE_MODE_COUNTER = 0;
                    return;

                }
            }
//            System.out.println("restartGSMState(): Try to disable Airplane Mode");
//            if (!_AIRPLANE_MODE) {
//                _ATC.send("AT+COPS=0\r", this);
//                _ATC.send("AT+CGATT=1\r",this);
//                for (int i = 0; i < 600; i++) {
//                    updateTimeCounter();
//                    synchronized (_MUTEX) {
//                        try {
//                            _MUTEX.wait(100);
//                        } catch (InterruptedException ex) {
//                            ex.printStackTrace();
//                        }
//                        _MUTEX.notifyAll();
//                    }
//                    if (_SERVICE == 1) {
//                        System.out.println("SERVICE=1");
//                        break;
//                    }
//                }
//                _STATE = _WORK_STATE;
//                _AIRPLANE_MODE_COUNTER = 0;
//                return;
//            }
        }
        _AIRPLANE_MODE_COUNTER++;
    }

    private void getCID() {
        String cmd = "";
        switch (_PLATFORM) {
            case (_PLATFORM_UNDEFINED):
                cmd = "ATI\r";
                break;
            case (_BGS5_PLATFORM):
                cmd = "AT+CCID\r";
                break;
            case (_EHS5_PLATFORM):
                cmd = "AT+CCID\r";
                break;
            case (_TC65_PLATFORM):
                cmd = "AT^SCID\r";
                break;
        }
        if (cmd.compareTo("") != 0) {
            _ATC.send(cmd, (ATCommandResponseListener) this);
        }

    }

    /**
     *
     * @param downloadresult
     */
    public void FileDownloaded(boolean downloadresult) {

        _FILE_UPDATED = downloadresult;
        if (_FILE_UPDATED) {
            _CURRENT_CONFIG = loadConfig();
            _FAR_WAYS.pause();
            _FAR_WAYS.clear();
            if (acceptConfig(_CURRENT_CONFIG, true)) {
                AkMessage2 m = new AkMessage2();
                m.set_AkNumber(_ObjectID);
                m.set_EventClass(5);
                m.set_EventCod(2);
                m.set_ZoneNumber(0);
                m.set_PartitionNumber(0);
                m.set_MesNumber(get_MessageNumber());
                System.out.println(m.toOko2String());
                synchronized (_MESSAGES) {
                    _MESSAGES.addElement(m);
                    _MESSAGES.notifyAll();
                }
            } 
        }        
        _FAR_WAYS.resume();
        _STATE = _PREV_STATE;
    }
    
    private void get_RegState(){
        _ATC.send("AT+CREG?\r", this);
    }
    
    private void get_IMSI(){
        _ATC.send("AT+CIMI\r", this);
    }
    private void get_CCID(){
        _ATC.send("AT+CCID?\r",this);
    }
}
