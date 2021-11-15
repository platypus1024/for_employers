using System;
using System.Collections.Generic;
using System.IO;
using System.Xml;
using System.Threading;
using XMLSpace;
using TelegramDLL;

using Newtonsoft.Json;

using System.Net;

using SStrSpace;
using Newtonsoft.Json.Linq;
using System.Linq;
using System.Text.RegularExpressions;

using System.Threading.Tasks;
using Telegram.Bot;
using Telegram.Bot.Args;
using Telegram.Bot.Types;
using Telegram.Bot.Types.ReplyMarkups;
using TgBot = Telegram.Bot;
using Message = Telegram.Bot.Types.Message;
using Telegram.Bot.Types.InputFiles;

using System.Runtime.InteropServices;
using System.Linq.Expressions;
using System.Text;
using System.Reflection;
using Telegram.Bot.Exceptions;

namespace TelegramBot
{
  [ComVisible(false)]
  public class Telegram
  {
    public delegate void SendMessageDelegate(Object sender, TelegramDLL.Message msg);
    public event SendMessageDelegate SendMessageEvent;

    public delegate void NotificationMessageDelegate(uint _res, int _messageid);
    public event NotificationMessageDelegate SendNotification;


    
    private static string ApiResponseEventArgsTypeName = typeof(ApiResponseEventArgs).Name;
    private static string CallbackQueryEventArgsTypeName = typeof(CallbackQueryEventArgs).Name;
    private static string MessageEventArgsTypeName = typeof(MessageEventArgs).Name;
    private static string TelegramDLLMessageTypeName = typeof(TelegramDLL.Message).Name;

    public delegate bool TestConnectDelegate(string address);
    public event TestConnectDelegate TestConnectEvent;

    private TelegramDLL.Tracer _TRACER = new TelegramDLL.Tracer();
    private TelegramBotClient Bot;
    private Dictionary<string, List<Int64>> _ROUTES_FOR_OBJECTS;
    //private Dictionary<string, Dictionary<string, Dictionary<string, string>>> _OKO2_SIGNALS_DICTIONARY;
    private Newtonsoft.Json.Linq.JObject _USERS;
    private Newtonsoft.Json.Linq.JObject _USERS_MID;
    private Newtonsoft.Json.Linq.JObject _GROUPS;

    public delegate void DebugMessageDelegate(TelegramDLL.Tracer.eLogLevel lolevel, string text);
    public event DebugMessageDelegate DebugMessageEvent;
    public string PCN_ADDRESS = "";
    const string rc = "QWERTYUIOPASDFGHJKLZXCVBNMqwertyuiopasdfghjklzxcvbnm0123456789";
    private Dictionary<Int64, Dictionary<uint, byte>> flags = new Dictionary<Int64, Dictionary<uint, byte>>();
    private Timer _TT;
    private static object flags_Locker = new object();
    private bool _RUN;
    private List<object> _QUE;
    private Thread _WORK_THREAD;
    static readonly Random rndGen = new Random();
    private ManualResetEvent _MessageReceivedEvent = new ManualResetEvent(false);
    private ManualResetEvent _ModuleThreadExitEvent = new ManualResetEvent(false);
    public Telegram()
    {
      _TRACER.LogFileName = "TelegramBot" + ".log";
      _QUE = new List<object>();
    }

    public Tracer.eLogLevel LogLevel
    {
      get { return _TRACER.LogLevel; }
      set { _TRACER.LogLevel = value; }
    }

    public int LogRotateDays                                                                        // Количество дней хранения файлов лога (дни) 0 - отключено
    {
      get { return _TRACER.LogRotateDays; }
      set { _TRACER.LogRotateDays = value; }
    }
    public int LogRotatePeriod                                                                      // Период ротации логов (дни)
    {
      get { return _TRACER.LogRotatePeriod; }
      set { _TRACER.LogRotatePeriod = value; }
    }

    public  bool Start()
    {


      //Assembly asm = Assembly.LoadFrom("Newtonsoft.Json.dll");
      /* if (_TT == null)
       {
         _TT = new Timer(timerWorker, null, 0, 1000);
       }*/
      
      if (Sec_Key == null || Sec_Key == "")
      {
        ModuleThread_DebugMessage(Tracer.eLogLevel.FATAL, String.Format("\r\n\t-------------------Telegram.Start()-------------------\r\n\t\t{0}\r\n\t--------------------------------------------------------------", new[] { Sec_Key }));
        return false;
      }
      else
      {
        ModuleThread_DebugMessage(Tracer.eLogLevel.FATAL, String.Format("\r\n\t-------------------Telegram.Start()-------------------\r\n\t\t{0}\r\n\t--------------------------------------------------------------", new[] { Sec_Key }));
      }
      try
      {
        if (_USERS == null)
        {
          LoadUsers();
        }
        if (_GROUPS == null)
        {
          LoadGroups();
        }
        //if(_ROUTES_FOR_OBJECTS == null)
        //{
        //  LoadObjectsRoutesTable();
        //}
        //if(_OKO2_SIGNALS_DICTIONARY == null)
        //{
        //  LoadOko2SignalsTable();
        //}
        if (Bot == null)
        {
          //LoadConfig();
         
          //client.BaseAddress = new Uri("http://api.telegram.org/bot");
          ModuleThread_DebugMessage(Tracer.eLogLevel.FATAL, String.Format("\r\n\t-------------------Telegram.Start()-------------------\r\n\t\t{0} {1}\r\n\t--------------------------------------------------------------", new[] { "set SecurityProtocol to ", ServicePointManager.SecurityProtocol.ToString() }));
          ServicePointManager.SecurityProtocol = SecurityProtocolType.Tls12;
          ModuleThread_DebugMessage(Tracer.eLogLevel.FATAL, String.Format("\r\n\t-------------------Telegram.Start()-------------------\r\n\t\t{0}\r\n\t--------------------------------------------------------------", new[] { "set Create Telegram Bot" }));
          Bot = new TelegramBotClient((Sec_Key));
          ModuleThread_DebugMessage(Tracer.eLogLevel.FATAL, String.Format("\r\n\t-------------------Telegram.Start()-------------------\r\n\t\t{0}\r\n\t--------------------------------------------------------------", new[] { "register Bot Handlers" }));
          try
          {
            Bot.OnMessage += BotOnMessageReceived;
            Bot.OnMessageEdited += BotOnMessageReceived;
            Bot.OnCallbackQuery += BotOnCallbackQueryReceived;
            Bot.OnInlineQuery += BotOnInlineQueryReceived;
            Bot.OnInlineResultChosen += BotOnChosenInlineResultReceived;
            Bot.OnReceiveError += BotOnReceiveError;
            Bot.ApiResponseReceived += BotOnApiResponseReceived;
            TimeSpan tm = Bot.Timeout;
          }
          catch (Exception ex)
          {
            ModuleThread_DebugMessage(Tracer.eLogLevel.FATAL, ex.ToString());
          }


          if (Bot.IsReceiving)
          {
            ModuleThread_DebugMessage(Tracer.eLogLevel.FATAL, "Bot is Receiving");
            return true;
          }
          else
          {
            TgBot.Types.User me = new User();
            //me = await Bot.GetMeAsync();
            try
            {
              Task<User> meTask = Bot.GetMeAsync();
              meTask.Wait();
              me = meTask.Result;
            }
            catch(Exception ex)
            {
              ModuleThread_DebugMessage(Tracer.eLogLevel.FATAL, ex.ToString());
              DebugMessageEvent?.Invoke(TelegramDLL.Tracer.eLogLevel.DEBUG, ex.ToString());
              
              _TRACER?.Write(Tracer.eLogLevel.DEBUG, ex.ToString());
              SendNotification(ResultCode.Module_StartError, 0);
              try
              {
                if (Bot != null)
                {
                  if (Bot.IsReceiving)
                  {
                    Bot.StopReceiving();
                  }
                }
              }catch(Exception ex1)
              {
                DebugMessageEvent?.Invoke(TelegramDLL.Tracer.eLogLevel.DEBUG, ex1.ToString());
                _TRACER?.Write(Tracer.eLogLevel.DEBUG, ex.ToString());
              }
              Bot = null;
              return false;
            }
            //TgBot.Types.Enums.UpdateType.
            try
            {
              Bot.StartReceiving();
            }catch(Exception ex)
            {
              ModuleThread_DebugMessage(Tracer.eLogLevel.FATAL, ex.ToString());
            }
            
            
            if (Bot.IsReceiving)
            {
              SendNotification(ResultCode.Module_Started, 0);
              ModuleThread_DebugMessage(Tracer.eLogLevel.FATAL, String.Format("\r\n\t-------------------Подключились к telegram--------------------\r\n\tName: {0}\r\n\this a Bot: {1}\r\n\tID: {2}\r\n\t--------------------------------------------------------------", new[] { me.Username, "" + me.IsBot, "" + me.Id }));
              //sendMessageToAll("Служба передачи сообщений через Telegram снова работает!\r\nПриятного использования!");
              sendToAdmins("Служба передачи сообщений через Telegram снова работает!\r\nПриятного использования!");
              _ModuleThreadExitEvent.Reset();
              if (_WORK_THREAD == null)
              {
                _WORK_THREAD = new Thread(WorkThread);
              }
              _RUN = true;
              _WORK_THREAD.Start();

              return true;
            }
            else
            {
              SendNotification(ResultCode.Module_StartError, 0);
              return false;
            }
            
            
          }
        }
      }
      catch (Exception ex)
      {
        _TRACER?.Write(Tracer.eLogLevel.DEBUG, ex.ToString());
        SendNotification(ResultCode.Module_StartError, 0);
        ModuleThread_DebugMessage(Tracer.eLogLevel.FATAL, String.Format("\r\n\t-------------------Telegram.Start()-------------------\r\n\tMessage: {0}\r\n\tStack: {1}\r\n\t--------------------------------------------------------------", new[] { ex.Message, ex.StackTrace }));
        while (ex.InnerException != null)
        {
          ModuleThread_DebugMessage(Tracer.eLogLevel.FATAL, String.Format("\r\n\tInner{0}\r\n\t--------------------------------------------------------------", new[] { ex.InnerException.ToString() }));
          ex = ex.InnerException;
        }
        DebugMessageEvent?.Invoke(TelegramDLL.Tracer.eLogLevel.DEBUG, ex.ToString());
      }
      return false;
    }

    private async void BotOnApiResponseReceived(object sender, ApiResponseEventArgs e)
    {
       string ee = e.ToString();
      var res = await e.ResponseMessage.Content.ReadAsStringAsync();
      ModuleThread_DebugMessage(Tracer.eLogLevel.FATAL, String.Format("\r\n\t-------------------BotOnApiResponseReceived-------------------\r\n\t\t{0}\r\n\t--------------------------------------------------------------", new[] { res }));
      if (e.ResponseMessage.ReasonPhrase.Contains("Unauthorized")) {

        //{ "ok":false,"error_code":401,"description":"Unauthorized"}
        SendNotification(ResultCode.Message_ServerError, 0);
        return;
      }              
    }



    public string BaseDirectory { get; set; }
    public string Sec_Key { get; set; }

    private bool LoadUsers()
    {
      try
      {
        System.IO.StreamReader sr = new System.IO.StreamReader(System.IO.Path.Combine(BaseDirectory, "Data\\users.json"));
        string jsonstring = sr.ReadToEnd();
        sr.Close();
        _USERS = JsonConvert.DeserializeObject<Newtonsoft.Json.Linq.JObject>(jsonstring);
        _USERS_MID = new JObject();
        Newtonsoft.Json.Linq.IJEnumerable<Newtonsoft.Json.Linq.JToken> users = _USERS.Children();
        foreach (Newtonsoft.Json.Linq.JToken user in users)
        {
          string name = user.Path;
          JObject userdata = new JObject();
          JProperty usermidprop = new JProperty("mid", 0);
          userdata.Add(usermidprop);
          _USERS_MID.Add(name, userdata);
        }
        return true;
      }
      catch (ArgumentException ex)
      {
        DebugMessageEvent?.Invoke(TelegramDLL.Tracer.eLogLevel.DEBUG, ex.ToString());
      }
      catch (FileNotFoundException ex)
      {
        DebugMessageEvent?.Invoke(TelegramDLL.Tracer.eLogLevel.DEBUG, ex.ToString());
      }
      catch (IOException ex)
      {
        DebugMessageEvent?.Invoke(TelegramDLL.Tracer.eLogLevel.DEBUG, ex.ToString());
      }
      catch (Exception ex)
      {
        DebugMessageEvent?.Invoke(TelegramDLL.Tracer.eLogLevel.DEBUG, ex.ToString());
      }

      return false;
    }

    private bool LoadGroups()
    {
      try
      {
        System.IO.StreamReader sr = new System.IO.StreamReader(System.IO.Path.Combine(BaseDirectory, "Data\\groups.json"));
        string jsonstring = sr.ReadToEnd();
        sr.Close();
        _GROUPS = JsonConvert.DeserializeObject<Newtonsoft.Json.Linq.JObject>(jsonstring);

        return true;
      }
      catch (ArgumentException ex)
      {
        DebugMessageEvent?.Invoke(TelegramDLL.Tracer.eLogLevel.DEBUG, ex.ToString());
      }
      catch (FileNotFoundException ex)
      {
        DebugMessageEvent?.Invoke(TelegramDLL.Tracer.eLogLevel.DEBUG, ex.ToString());
      }
      catch (IOException ex)
      {
        DebugMessageEvent?.Invoke(TelegramDLL.Tracer.eLogLevel.DEBUG, ex.ToString());
      }
      catch (Exception ex)
      {
        DebugMessageEvent?.Invoke(TelegramDLL.Tracer.eLogLevel.DEBUG, ex.ToString());
      }
      if(_GROUPS == null)
      {
        _GROUPS = new JObject();
        saveGroups();
      }
      return false;
    }

    private bool LoadObjectsRoutesTable()
    {
      try
      {
        System.IO.StreamReader sr = new System.IO.StreamReader(System.IO.Path.Combine(BaseDirectory, "objectsRoutes.json"));
        string jsonstring = sr.ReadToEnd();
        _ROUTES_FOR_OBJECTS = JsonConvert.DeserializeObject<Dictionary<string, List<Int64>>>(jsonstring);
        return true;
      }
      catch (ArgumentException ex)
      {
        DebugMessageEvent?.Invoke(TelegramDLL.Tracer.eLogLevel.DEBUG, ex.ToString());
      }
      catch (FileNotFoundException ex)
      {
        DebugMessageEvent?.Invoke(TelegramDLL.Tracer.eLogLevel.DEBUG, ex.ToString());

      }
      catch (IOException ex)
      {
        DebugMessageEvent?.Invoke(TelegramDLL.Tracer.eLogLevel.DEBUG, ex.ToString());
      }
      catch (Exception ex)
      {
        DebugMessageEvent?.Invoke(TelegramDLL.Tracer.eLogLevel.DEBUG, ex.ToString());
      }


      return false;
    }

    public bool LoadConfig(string filename)
    {
      try
      {
        if (String.IsNullOrEmpty(filename))
          return false;
        XmlDocument config = new XmlDocument();
        config.Load(System.IO.Path.Combine(BaseDirectory, filename));
        Sec_Key = XML.GetValue(config, "/config/TLG/Key");
        //tracer.loglevel = (tracer.eloglevel)int.parse(xml.getvalue(config, "/config/loglevel"));
        //tracer.logrotatedays = int.parse(xml.getvalue(config, "/config/logrotatedays"));
        //tracer.logrotateperiod = int.parse(xml.getvalue(config, "/config/logrotateperiod"));

        //cinetaddresspcn = xml.getvalue(config, "/config/cfgpcn/address");
        //cinet.virtualip = xml.getvalue(config, "/config/cfgpcn/login");
        //cinet.outport = int.parse(xml.getvalue(config, "/config/cfgpcn/port"));

        //cinet.restoreconnectiontime = int.parse(xml.getvalue(config, "/config/cfgpcn/restoreconnectiontime"));
        //cinet.keepalivetime = int.parse(xml.getvalue(config, "/config/cfgpcn/keepalivetime"));


        //inetaddresspcn = xml.getvalue(config, "/config/pcn/address");
        //inet.virtualip = xml.getvalue(config, "/config/pcn/login");
        //inet.outport = int.parse(xml.getvalue(config, "/config/pcn/port"));

        //inet.restoreconnectiontime = int.parse(xml.getvalue(config, "/config/pcn/restoreconnectiontime"));
        //inet.keepalivetime = int.parse(xml.getvalue(config, "/config/pcn/keepalivetime"));
        if (Sec_Key != "")
        {
          return true;
        }
        return false;
      }
      catch (Exception ex)
      {
        DebugMessageEvent?.Invoke(TelegramDLL.Tracer.eLogLevel.DEBUG, ex.ToString());
      }
      return false;
    }

    public void ReceiveMessage(TelegramDLL.Message msg)
    {
      lock (_QUE)
      {
        _QUE.Add(msg);
        _MessageReceivedEvent.Set();
      }
    }

    private void ReciveMessageHandler(TelegramDLL.Message msg)
    {
      try
      {
        ModuleThread_DebugMessage(Tracer.eLogLevel.DEBUG, "\r\n\t-------------------Принято новое сообщение от ПЦН!-------------------\r\n\tType: " + msg.Type.ToString() + "\r\n\tMessage: " + msg.Text.ToString() + "\r\n\tAddress: " + msg.Address.ToString() + "\r\n\t---------------------------------------------------------------------");
        bool sendToAll = false;
        if (!string.IsNullOrEmpty(msg.Get("ToAll").ToString()))
        {
          try
          {
            if (Boolean.Parse(msg.Get("ToAll").ToString()))
            {
              sendToAll = true;
            }
          }
          catch (Exception ex)
          {

          }

        }
        string addr = msg.Address;
        if (string.IsNullOrEmpty(addr) && sendToAll == false)
        {
          SendNotification(ResultCode.Message_AddressToRequired, msg.Id);
          return;
        }

        if (addr.ToLower() == "self")
        {
          if (Bot != null)
          {
            if (Bot.IsReceiving)
            {
              SendNotification(ResultCode.Message_Sent, msg.Id);
              SendNotification(ResultCode.Message_Delivered, msg.Id);
              if (SendMessageEvent != null)
              {
                SendMessageEvent?.BeginInvoke(this, msg, null, null);
              }

              return;
            }
            else
            {
              SendNotification(ResultCode.Message_ServerError, msg.Id);
              return;
            }
          }
          else
          {
            SendNotification(ResultCode.Fail, msg.Id);
            return;
          }
        }

        string pattern = "^[+]?([0-9]+)";
        MatchCollection x = Regex.Matches(addr, pattern);
        bool sendtogroupe = false;
        if (x != null)
        {

          if (((MatchCollection)x).Count == 1)
          {
            if (x[0].Groups.Count == 2)
            {
              addr = x[0].Groups[1].Value;
            }
            else if (x[0].Groups.Count == 0)
            {
              addr = x[0].Value;
            }

          }
          else if (((MatchCollection)x).Count == 0)
          { sendtogroupe = true; }
        }
        if (msg.Type == TelegramDLL.MessageType.TEXT_TLG)
        {
          string text = msg.Get("Text").ToString();
          string attachment = msg.Get("Attachment").ToString();
          string location = msg.Get("Location").ToString();
          bool istext = !string.IsNullOrEmpty(text);
          bool isattach = !string.IsNullOrEmpty(attachment);
          bool islocation = !string.IsNullOrEmpty(location);
          bool sendById = false;
          if (!string.IsNullOrEmpty(msg.Get("ById").ToString()))
          {
            try
            {
              if (Byte.Parse(msg.Get("ById").ToString()) == 1)
              {
                sendById = true;
              }
            }
            catch (Exception ex)
            {

            }

          }
          TelegramDLL.Message answer = new TelegramDLL.Message();
          answer.Type = TelegramDLL.MessageType.NOTIFICATION_DRV_TLG;
          answer.Id = msg.Id;
          ModuleThread_DebugMessage(Tracer.eLogLevel.DEBUG, "\r\n\t-------------------Принято новое сообщение от ПЦН!-------------------\r\n\tMessage: " + text + "\r\n\tAddress: " + addr + "\r\n\tSendToGroupe: "+sendtogroupe+"\r\n\tSendById: "+sendById + "\r\n\t---------------------------------------------------------------------");


          if (istext && !isattach && !islocation)//100 text
          {
            if (sendToAll)
            {
              sendMessageToAll(text);
              if (SendNotification != null)
              {
                SendNotification(TelegramDLL.ResultCode.Message_Sent, msg.Id);
                return;
              }
            }
            else if (sendById)
            {
              if (sendMessageToId(long.Parse(addr), text))
              {
                if (SendNotification != null)
                {
                  SendNotification(TelegramDLL.ResultCode.Message_Sent, msg.Id);
                  return;
                }
              }
            }
            else
            {
              if (sendTextMessageTo(addr, text, sendtogroupe, msg.Id))
              {
                if (SendNotification != null)
                {
                  SendNotification(TelegramDLL.ResultCode.Message_Sent, msg.Id);
                  return;
                }
              }
            }

          }
          else if(!istext && !isattach && islocation)//001 location
          {
            if (sendLocationTo(addr, location, sendtogroupe, msg.Id))
            {
              if (SendNotification != null)
              {
                SendNotification(TelegramDLL.ResultCode.Message_Sent, msg.Id);
                return;
              }
            }
          }
          else if (!istext && isattach && !islocation)//010 attach
          {
            if (sendAttachmentTo(addr, attachment, sendtogroupe, "", msg.Id))
            {
              if (SendNotification != null)
              {
                SendNotification(TelegramDLL.ResultCode.Message_Sent, msg.Id);
                return;
              }
            }
          }
          else if (!istext && isattach && islocation)//011 attach + location
          {
            if (sendAttachmentTo(addr, attachment, sendtogroupe, "", msg.Id) && sendLocationTo(addr, location, sendtogroupe, msg.Id))
            {
              if (SendNotification != null)
              {
                SendNotification(TelegramDLL.ResultCode.Message_Sent, msg.Id);
                return;
              }
            }
          }
          else if (istext && !isattach && islocation)//101 text+location
          {
            if (sendTextMessageTo(addr, text, sendtogroupe, msg.Id) && sendLocationTo(addr, location, sendtogroupe, msg.Id))
            {
              if (SendNotification != null)
              {
                SendNotification(TelegramDLL.ResultCode.Message_Sent, msg.Id);
                return;
              }
            }
          }
          else if (istext && isattach && !islocation)//110 text+attach
          {
            if (sendTextMessageTo(addr, text, sendtogroupe, msg.Id) && sendAttachmentTo(addr, attachment, sendtogroupe, "", msg.Id))//
            {
              if (SendNotification != null)
              {
                SendNotification(TelegramDLL.ResultCode.Message_Sent, msg.Id);
                return;
              }
            }
          }
          else if (istext && isattach && islocation)//111 text+attach+location
          {
            if (sendTextMessageTo(addr, text, sendtogroupe, msg.Id) && sendAttachmentTo(addr, attachment, sendtogroupe, "", msg.Id) && sendLocationTo(addr, location, sendtogroupe, msg.Id))
            {
              if (SendNotification != null)
              {
                SendNotification(TelegramDLL.ResultCode.Message_Sent, msg.Id);
                return;
              }
            }
          }
          else if (!istext && !isattach && !islocation)//000 ERROR
          {
            if (SendNotification != null)
            {
              SendNotification(TelegramDLL.ResultCode.Message_SendError, msg.Id);
            }
            return;
          }

          if (SendNotification != null)
          {
            SendNotification(TelegramDLL.ResultCode.Message_SendError, msg.Id);
          }
          return;
        }
      }
      catch (Exception ex)
      {
        _TRACER?.Write(Tracer.eLogLevel.DEBUG, ex.ToString());
        try
        {
          if (SendNotification != null)
          {
            SendNotification(TelegramDLL.ResultCode.Message_SendError, msg.Id);
          }
        }
        catch (Exception ex1)
        {
          _TRACER?.Write(Tracer.eLogLevel.DEBUG, ex1.ToString());
        }
      }
    }
    

    private void sendTo(string addr, string data, bool togroup)
    {

    }

    private void BotOnReceiveError(object sender, ReceiveErrorEventArgs e)
    {
      ModuleThread_DebugMessage(Tracer.eLogLevel.FATAL, "\r\n\tОшибка приёма сообщения Telegram\r\n\tMessage: " + e.ApiRequestException.Message + "\r\n\tTrace: " + e.ApiRequestException.StackTrace);
      return;
      //throw new NotImplementedException();
    }

    private void BotOnChosenInlineResultReceived(object sender, ChosenInlineResultEventArgs e)
    {
      ModuleThread_DebugMessage(Tracer.eLogLevel.FATAL, "\r\n\tПолучен ChosenInlineResult  от:\r\n\tUsername: " + e.ChosenInlineResult.From.Username + "\r\n\tUserID:" + e.ChosenInlineResult.From.Id + "\r\n\tResultID: " + e.ChosenInlineResult.ResultId + "\r\n\tЗапрос:" + e.ChosenInlineResult.Query);
      return;
      //throw new NotImplementedException();
    }

    private void BotOnInlineQueryReceived(object sender, InlineQueryEventArgs e)
    {
      ModuleThread_DebugMessage(Tracer.eLogLevel.FATAL, "\r\n\tПолучен Inline запрос от:\r\n\tUsername: " + e.InlineQuery.From.Username + "\r\n\tUserID:" + e.InlineQuery.From.Id + "\r\n\tQueryID: " + e.InlineQuery.Id + "\r\n\tЗапрос:" + e.InlineQuery.Query);

      return;
      //throw new NotImplementedException();
    }

    private void BotOnCallbackQueryReceived(object sender, CallbackQueryEventArgs e)
    {
      ModuleThread_DebugMessage(Tracer.eLogLevel.FATAL, "\r\n\tПолучен CallbackQuery от\r\n\tusername: " + e.CallbackQuery.From.Username + "\r\n\tUserID: " + e.CallbackQuery.From.Id + "\r\n\tText: " + e.CallbackQuery.Data + "\r\n\tChatID: " + e.CallbackQuery.Message.Chat.Id + "\r\n\tMessageID: " + e.CallbackQuery.Message.MessageId);
      lock (_QUE)
      {
        _QUE.Add(e);
        _MessageReceivedEvent.Set();
      }
      //throw new NotImplementedException();
    }

    private void BotOnMessageReceived(object sender, MessageEventArgs e)
    {
      ModuleThread_DebugMessage(Tracer.eLogLevel.FATAL, "\r\n\tПолученно сообщение от\r\n\tusername: " + e.Message.From.Username + "\r\n\tUserID: " + e.Message.From.Id + "\r\n\tText: " + e.Message.Text + "\r\n\tChatID: " + e.Message.Chat.Id + "\r\n\tMessageID: " + e.Message.MessageId);
      lock (_QUE)
      {
        _QUE.Add(e);
        _MessageReceivedEvent.Set();
      }
      //throw new NotImplementedException();
    }

    private void saveUsers()
    {
      string users = JsonConvert.SerializeObject(_USERS, Newtonsoft.Json.Formatting.Indented);
      string writeto = System.IO.Path.Combine(System.IO.Path.Combine(BaseDirectory, "Data"), "users.json");
      System.IO.File.WriteAllText(writeto, users);
    }

    private bool addGroup(string name, long id)
    {

      bool res = false;
      try
      {
        if (_GROUPS == null) { return false; }
        lock (_GROUPS)
        {
          if (_GROUPS.ContainsKey(name)) { return false; }
          JObject g = new JObject();
          g.Add("Id", id);
          _GROUPS.Add(name, g);
        }
        saveGroups();
        res = true;
      }catch(Exception ex)
      {
        _TRACER?.Write(Tracer.eLogLevel.DEBUG, ex.ToString());
      }

      return res;
    }

    private bool removeGroup(string name)
    {
      bool res = true;
      if (_GROUPS == null) {return false; }
      try
      {
        lock (_GROUPS)
        {
          if (!_GROUPS.ContainsKey(name)) { return true; }
          if (_GROUPS.Remove(name))
          {
            saveGroups();
          }
          else
          {
            res = false;
          }
        }
      }
      catch(Exception ex)
      {
        _TRACER?.Write(Tracer.eLogLevel.DEBUG, ex.ToString());
      }


      return res;
    }
    private void saveGroups()
    {
      if (_GROUPS == null) { return; }
      try
      {
        string groups = "";
        string writeto = "";
        lock (_GROUPS)
        {
          groups = JsonConvert.SerializeObject(_GROUPS, Newtonsoft.Json.Formatting.Indented);
          writeto = System.IO.Path.Combine(System.IO.Path.Combine(BaseDirectory, "Data"), "groups.json");
        }
        System.IO.File.WriteAllText(writeto, groups);
      }
      catch(Exception ex)
      {
        _TRACER?.Write(Tracer.eLogLevel.DEBUG, ex.ToString());
      }
     
    }
    private bool saveUsers(Newtonsoft.Json.Linq.JObject users)
    {
      bool res = false;
      try
      {
        string nusers = JsonConvert.SerializeObject(users, Newtonsoft.Json.Formatting.Indented);
        string writeto = System.IO.Path.Combine(System.IO.Path.Combine(BaseDirectory, "Data"), "users.json");
        System.IO.File.WriteAllText(writeto, nusers);
        res = true;
      } catch (Exception ex)
      {

      }

      return res;
    }

    private bool UpdateUser(Contact contact)
    {
      foreach (string name in _USERS.Next)
      {

      }

      return false;
    }

    public void Stop()
    {
      try
      {
        _ModuleThreadExitEvent.Set();
      }catch(ObjectDisposedException ex)
      {
        _TRACER?.Write(Tracer.eLogLevel.DEBUG, ex.ToString());
      }
      
      if (_WORK_THREAD != null)
      {
        try
        {
          if (_WORK_THREAD.IsAlive == true)
            if (_WORK_THREAD.Join(7000) == false)                    // Ждать завершения потока
            {
              _WORK_THREAD.Abort();                                  // Если не дождались, то принудительно завершить
              _WORK_THREAD.Join(5000);
            }
        }
        catch(Exception ex)
        {
          _TRACER?.Write(Tracer.eLogLevel.DEBUG, ex.ToString());
        }
      }
      
    }

    private uint getMessageId(string username)
    {
      uint res = 0;
      JToken messid;
      JToken user;
      try
      {
        if (_USERS_MID == null) { return 0; }
        if (_USERS_MID.TryGetValue(username, out user))
        {
          if (user != null)
          {
            if (((JObject)user).TryGetValue("mid", out messid))
            {
              var mmid = _USERS.SelectToken(username).Value<uint>("mid");
              var id = (uint)mmid;

              if (id == 0xFFFF)
              {
                id = 0;
                ((JObject)_USERS_MID.SelectToken(username))["mid"] = id;

              }
              else
              {
                ((JObject)_USERS_MID.SelectToken(username))["mid"] = id + 1;
              }
              res = id;

            }
            else
            {
              ((JObject)_USERS_MID.SelectToken(username)).Add("mid", (UInt16)0);
              res = 0;
            }
          }
        }
      }
      catch(Exception ex)
      {
        _TRACER?.Write(Tracer.eLogLevel.DEBUG, ex.ToString());
        return 0;
      }
      return res;
    }

    private string GetRandomPassword(int pwdLength)
    {
      char[] letters = rc.ToCharArray();
      string s = "";
      for (int i = 0; i < pwdLength; i++)
      {
        s += letters[rndGen.Next(letters.Length)].ToString();
      }
      return s;
    }

    private InlineKeyboardMarkup getMainMenu()
    {
      string[] files = System.IO.Directory.GetFiles(System.IO.Path.Combine(BaseDirectory, "Menu"));
      DirectoryInfo di = new DirectoryInfo(System.IO.Path.Combine(BaseDirectory, "Menu"));
      string[] dirs = System.IO.Directory.GetDirectories(System.IO.Path.Combine(BaseDirectory, "Menu"));
      List<InlineKeyboardButton[]> listofkeys = new List<InlineKeyboardButton[]>(0);
      foreach (DirectoryInfo d in di.GetDirectories())
      {
        //string path = d.FullName.Replace('\\','/');
        var k = InlineKeyboardButton.WithCallbackData(d.Name, "GoTo Menu\\" + d.Name);
       
        //k.Url = d.FullName;
        listofkeys.Add(new InlineKeyboardButton[] { k });
      }
      listofkeys.Add(new InlineKeyboardButton[] { InlineKeyboardButton.WithCallbackData("Назад", "backToMainMenu") });
      listofkeys.Add(new InlineKeyboardButton[] { InlineKeyboardButton.WithCallbackData("Отмена", "hide") });
      InlineKeyboardButton[][] btns = listofkeys.ToArray();
      var inlineKeyboard = new InlineKeyboardMarkup(btns);
      return inlineKeyboard;
    }

    private InlineKeyboardMarkup getNextMenu(string path)
    {
      var sep = System.IO.Path.AltDirectorySeparatorChar;
      DirectoryInfo di = new DirectoryInfo(System.IO.Path.Combine(BaseDirectory, path.Replace('/', '\\')));
      List<InlineKeyboardButton[]> listofkeys = new List<InlineKeyboardButton[]>(0);
      if (path.IndexOf("Документация") > 0)
      {
        foreach (DirectoryInfo d in di.GetDirectories())
        {
          //string path = d.FullName.Replace('\\','/');
          int idx = path.IndexOf("Документация");
          int offset = "Документация".Length;
          int fullidx = idx + offset;
          string forname = path.Substring(fullidx);
          var k = InlineKeyboardButton.WithCallbackData(d.Name, "{\"GetDoc\": {\"F\":\"" + d.Name + "\"}}");
          //k.Url = d.FullName;
          listofkeys.Add(new InlineKeyboardButton[] { k });
        }
        foreach (FileInfo f in di.GetFiles())
        {
          //string path = d.FullName.Replace('\\','/');
          var k = InlineKeyboardButton.WithCallbackData(f.Name, "{\"Get\": \"" + path + "/" + f.Name + "\"}");
          //k.Url = d.FullName;
          listofkeys.Add(new InlineKeyboardButton[] { k });
        }
      }
      else
      {
        foreach (DirectoryInfo d in di.GetDirectories())
        {
          //string path = d.FullName.Replace('\\','/');
          var k = InlineKeyboardButton.WithCallbackData(d.Name, "{\"GoTo\": \"" + path + "/" + d.Name + "\"}");
          //k.Url = d.FullName;
          listofkeys.Add(new InlineKeyboardButton[] { k });
        }
        foreach (FileInfo f in di.GetFiles())
        {
          //string path = d.FullName.Replace('\\','/');
          var k = InlineKeyboardButton.WithCallbackData(f.Name, "{\"Get\": \"" + path + "/" + f.Name + "\"}");
          //k.Url = d.FullName;
          listofkeys.Add(new InlineKeyboardButton[] { k });
        }
      }
      var parent = di.Parent.Name;
      if (di.Parent.FullName.IndexOf("Menu") > 0)
      {
        var p = di.Parent.FullName.Substring(di.Parent.FullName.IndexOf("Menu"));
        listofkeys.Add(new InlineKeyboardButton[] { InlineKeyboardButton.WithCallbackData("Назад", "{\"GoTo\": \"" + p + "\"}") });
      }
      listofkeys.Add(new InlineKeyboardButton[] { InlineKeyboardButton.WithCallbackData("Отмена", "hide") });
      InlineKeyboardButton[][] btns = listofkeys.ToArray();
      var inlineKeyboard = new InlineKeyboardMarkup(btns);
      return inlineKeyboard;
    }

    private string getPathForNums(string way)
    {
      string res = "";
      string[] pathitems = way.Split('.');
      
      for(int i = 0; i < pathitems.Count(); i++)
      {

      }
      return res;
    }

    private MenuItem getMenuFor(string menufor, string ID="")
    {

      string sep = "";
      MenuItem res = new MenuItem();
      if(menufor == null) { return res; }
      if(!string.IsNullOrEmpty(menufor))
      {
        sep = ".";
      }
      string[] pathitems = menufor.Split('.');
      DirectoryInfo di = new DirectoryInfo(System.IO.Path.Combine(BaseDirectory, "Menu"));
      List<InlineKeyboardButton[]> listofkeys = new List<InlineKeyboardButton[]>(0);
      FileSystemInfo[] diritems = di.GetFileSystemInfos();
      string text = "Выберите вариант:\r\n";
      
     // if(pathitems.Count() == 1)
      {
        for(int y =0; y < pathitems.Count(); y++)
        {
          if (!string.IsNullOrEmpty(pathitems[y]))
          {
            int idx = int.Parse(pathitems[y]);
            diritems = ((DirectoryInfo)diritems[idx]).GetFileSystemInfos();
          }
        }
        List<InlineKeyboardButton> numerickeys = new List<InlineKeyboardButton>();
        for (int i = 0; i < diritems.Count(); i++)
        {
          JObject root = new JObject();
          JObject cmd = new JObject();
          //cmd.Add("F", menufor);
          //cmd.Add("Layer", d.Name);
          text += "" + (i+1) + " - " + diritems[i].Name + "\r\n";
          Type t = diritems[i].GetType();
          if(t.Name == "DirectoryInfo")
          {
            root.Add("GetDoc", menufor + sep + i);
          }else if (t.Name == "FileInfo")
          {
            root.Add("GetFile", menufor + sep + i);
          }
          
          string strcmd = JsonConvert.SerializeObject(root, Newtonsoft.Json.Formatting.None);
          var k = InlineKeyboardButton.WithCallbackData(String.Format("{0}", (i+1)), strcmd);
          numerickeys.Add(k);
        }
        listofkeys.Add(numerickeys.ToArray());
      }
     // else
      {

      }

      /* foreach (DirectoryInfo d in di.GetDirectories())
       {

         //string path = d.FullName.Replace('\\','/');
         JObject root = new JObject();
         JObject cmd = new JObject();
         cmd.Add("F", menufor);
         cmd.Add("L", d.Name);

         root.Add("GetDoc", cmd);
         string strcmd = JsonConvert.SerializeObject(root, Newtonsoft.Json.Formatting.None);
         var k = InlineKeyboardButton.WithCallbackData(d.Name, strcmd);
         k.SwitchInlineQuery = d.Name;
         listofkeys.Add(new InlineKeyboardButton[] { k });
       }*/
      if (pathitems.Count() >= 1 && !string.IsNullOrEmpty(menufor))
      {
        string backcmd = "";
        if (pathitems.Count() > 1)
        {
          string[] ph = new string[pathitems.Count() - 1];
          Array.Copy(pathitems, ph, ph.Length);
          backcmd = String.Join(".", ph);
        }

        listofkeys.Add(new InlineKeyboardButton[] { InlineKeyboardButton.WithCallbackData("Назад", "{\"GetDoc\":\""+backcmd+"\"}") });
      }
      if (ID != null)
      {
        if (!string.IsNullOrEmpty(ID))
        {
          if (_USERS.ContainsKey(ID))
          {
            if (_USERS.SelectToken(ID)["role"].Contains("admin"))
            {
              listofkeys.Add(new InlineKeyboardButton[] { InlineKeyboardButton.WithUrl("Техподдержка OKO", "https://t.me/joinchat/E2F9HhipxPKtBLu-KZR-Cw") });
            }
          }

        }
      }
      listofkeys.Add(new InlineKeyboardButton[] { InlineKeyboardButton.WithCallbackData("Отмена", "hide") });
      InlineKeyboardButton[][] btns = listofkeys.ToArray();
      var inlineKeyboard = new InlineKeyboardMarkup(btns);
      res.Keyboard = inlineKeyboard;
      res.Text = text;
      return res;
    }

    private FileItem getDocFileFor(string filefor)
    {
      FileItem res = new FileItem();
      if(filefor == null) { return res; }
      if(filefor == "") { return res; }
      string[] pathitems = filefor.Split('.');
      DirectoryInfo di = new DirectoryInfo(System.IO.Path.Combine(BaseDirectory, "Menu"));      
      FileSystemInfo[] diritems = di.GetFileSystemInfos();
      for (int y = 0; y < pathitems.Count(); y++)
      {
        if (pathitems[y] != "")
        {
          int idx = int.Parse(pathitems[y]);
          if(diritems[idx].GetType().Name != "FileInfo")
          {
            diritems = ((DirectoryInfo)diritems[idx]).GetFileSystemInfos();
          }
          else
          {
            res.Name = ((FileInfo)diritems[idx]).Name;
            res.Path = ((FileInfo)diritems[idx]).FullName;
            break;
          }
          
        }
      }
      return res;
    } 

    private ReplyKeyboardMarkup getStartKeyboard()
    {
      List<KeyboardButton> keybs = new List<KeyboardButton>(0);
      KeyboardButton req = new KeyboardButton("Меню");
      keybs.Add(req);
      ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup(keybs.ToArray(), resizeKeyboard: true, oneTimeKeyboard: true);
      keyboard.Selective = true;
      return keyboard;
    }
    private string getUserNameByID(Int64 id)
    {
      string username = "";
      if (_USERS == null) return username;
      try
      {
        foreach (var user in _USERS.Children())
        {
          string usr = user.ToString();
          if ((Int64)_USERS.SelectToken(user.Path)["chatid"] == id)
          {
            if (_USERS.ContainsKey(id + ".username"))
            {
              username = (string)_USERS.SelectToken("" + id)["username"];
            }
            break;
          }
        }
      }
      catch (Exception ex)
      {
        _TRACER?.Write(Tracer.eLogLevel.DEBUG, ex.ToString());
      }
      return username;
    }

    private string getUserNameByPhone(string phone)
    {
      string res = "";

      return res;
    }

    private int getUserIdByPhone(string phone)
    {
      int res = -1;
      string ph = "";
      try
      {
        if (phone.StartsWith("+"))
        {
          phone = phone.Substring(1);
        }
        Newtonsoft.Json.Linq.IJEnumerable<Newtonsoft.Json.Linq.JToken> users = _USERS.Children();
        foreach (Newtonsoft.Json.Linq.JToken user in users)
        {
          string name = user.Path;         

          ph = _USERS.SelectToken(name).Value<string>("phone");
          if (ph.StartsWith("+"))
          {
            ph = ph.Substring(1);
          }
          if (phone == ph)
          {
            res = _USERS.SelectToken(name).Value<int>("chatid");
            return res;
          }

        }
      }catch(Exception ex)
      {
        res = 0;
      }
        return res;
    }
    private  string GetPass(int x)
    {
      string pass = "";
      var r = new Random();
      while (pass.Length < x)
      {
        Char c = (char)r.Next(33, 125);
        if (Char.IsLetterOrDigit(c))
          pass += c;
      }
      return pass;
    }
    
    private long getGroupIdByName(string name)
    {
      long res = 0;
      try
      {
        Newtonsoft.Json.Linq.IJEnumerable<Newtonsoft.Json.Linq.JToken> groups = _GROUPS.Children();
        if (_GROUPS.ContainsKey(name))
        {
          res =(long) _GROUPS.SelectToken(name)["Id"];
          return res;
        }
      }
      catch (Exception ex)
      {
        _TRACER?.Write(Tracer.eLogLevel.DEBUG, ex.ToString()+"\r\n"+ex.StackTrace);
        res = 0;
      }
      return res;
    }
    private async void sendMessageToAll(string message)
    {
      if (_USERS != null)
      {
        Newtonsoft.Json.Linq.IJEnumerable<Newtonsoft.Json.Linq.JToken> users = _USERS.Children();
        foreach (Newtonsoft.Json.Linq.JToken user in users)
        {
          string name = user.Path;
          int id = _USERS.SelectToken(name).Value<int>("chatid");
          if (Bot != null)
          {
            if (Bot.IsReceiving)
            {
              try
              {
                Message m = await Bot.SendTextMessageAsync(id, message);
              }
              catch (Exception ex)
              {
                _TRACER?.Write(Tracer.eLogLevel.FATAL, ex.ToString()+"\r\n"+ex.StackTrace);
              }
            }           
          }
        }
      }
      if (_GROUPS != null)
      {
        Newtonsoft.Json.Linq.IJEnumerable<Newtonsoft.Json.Linq.JToken> groups = _GROUPS.Children();
        foreach (Newtonsoft.Json.Linq.JToken group in groups)
        {
          string name = group.Path;
          long id = _GROUPS.SelectToken(name).Value<long>("Id");
          bool trysendnext = false;
          if (Bot != null)
          {
            try
            {
              Message m = await Bot.SendTextMessageAsync(id, message);
            }
            catch (ChatNotFoundException ex)
            {
              _TRACER?.Write(Tracer.eLogLevel.FATAL, ex.ToString());

            }
            catch (ApiRequestException ex)
            {

              _TRACER?.Write(Tracer.eLogLevel.FATAL, ex.ToString());
              long chid = ex.Parameters.MigrateToChatId;
              if (chid != null)
              {
                if (chid != 0)
                {
                  _GROUPS.SelectToken(name)["Id"] = chid;

                  id = chid;
                  trysendnext = true;
                  saveGroups();
                }

              }


            }
            catch (Exception ex)
            {
              _TRACER?.Write(Tracer.eLogLevel.FATAL, ex.ToString());
            }
            if (trysendnext)
            {
              try
              {
                Message m = await Bot.SendTextMessageAsync(id, message);
              }
              catch (ApiRequestException ex)
              {

                _TRACER?.Write(Tracer.eLogLevel.FATAL, ex.ToString());
              }
              catch (Exception ex)
              {
                _TRACER?.Write(Tracer.eLogLevel.FATAL, ex.ToString());
              }
            }
          }
        }
      }

    }

    private  void sendAttachmentToPhone(string phone, string[] attachment = null)
    {
      bool res = true;
      if (_USERS == null)
      {
        return ;
      }
      if(attachment == null)
      {
        return;
      }
      if (attachment.Length == 0) { return; }
      int id = getUserIdByPhone(phone);
      if (id != 0)
      {
        foreach(string url in attachment)
        {
          sendAttachmentToId(id, url);
        }
       
      }

        return ;
    }
    
    private void sendAttachmentToGroup(string name, string[] attachment = null)
    {
      bool res = true;
      if (_GROUPS == null)
      {
        return;
      }
      if(attachment == null) { return; }
      if (attachment.Count() == 0) 
      { 

        return; 
      }
      long id = getGroupIdByName(name);
      if (id != 0)
      {
        foreach(string url in attachment)
        {
          sendAttachmentToId(id, url);
        }
        
      }
      return;
    }

    private bool sendAttachmentTo(string dest, string attachment, bool sendtogroup = false, string caption = "", int msg_id=0)
    {
      bool res = true;
      string[] _atts = SStr.GetFields(attachment, SStr.group_All).Fields.ToArray<string>();
      long id = 0;
      if (!sendtogroup)
      {
        id = getUserIdByPhone(dest);
      }
      else
      {
        id = getGroupIdByName(dest);
      }
      if(id <= 0)
      {

        if (SendNotification != null)
        {
          SendNotification(ResultCode.Message_AddressToError, msg_id);
        }
        return false;
      }
      int i = 0;
      foreach(string url in _atts)
      {
        res &= sendAttachmentToId(id, url,caption+ "\r\nФайл "+(i+=1)+"/"+_atts.Count() );
      }

      return res;
    }
    
    private bool sendAttachmentToId(long id, string url, string caption = "")
    {
      bool res = true;
      // Bot.SendTextMessageAsync(id, "Передача файлов начата.");
        
        FileInfo fi = new FileInfo(url);
        if (fi == null) { return false; }
        Object x = null;
        if (fi.Exists)
        {
          InputOnlineFile f = new InputOnlineFile(fi.Open(FileMode.Open, FileAccess.Read, FileShare.Read), fi.Name);
          string ext = fi.Extension.ToUpper();
        {
          switch (ext)
          {
            case ".PNG":
            case ".JPG":
            case ".BMP":
            case ".GIF":
              if (caption != "")
              {
                x = Bot.SendPhotoAsync(id, f, caption);
              }
              else{
                x = Bot.SendPhotoAsync(id, f);
              }
                                           
              break;
            case ".MP4":
              if (caption != "")
              {
                x = Bot.SendVideoAsync(id, f,0,0,0,caption);
              }
              else
              {
                x = Bot.SendVideoAsync(id, f);
              }
              
              break;
            case ".DOC":
            case ".EML":
            case ".XLS":
            case ".XML":
            case ".ODF":
            case ".PDF":
            case ".TXT":
            case ".RTF":
            case ".DOCX":
              if (caption != "")
              {
                x = Bot.SendDocumentAsync(id, f,caption);
              }
              else
              {
                x = Bot.SendDocumentAsync(id, f);
              }
              
              break;
            case ".WAV":
            case ".MP3":
            case ".VOC":
            case ".OGG":
              if (caption != "")
              {
                x = Bot.SendAudioAsync(id, f, caption);
              }
              else
              {
                x = Bot.SendAudioAsync(id, f);
              }
              
              break;
              /*x = await Bot.SendVoiceAsync(id, f);
              break;*/
          }
        }
          try
          {

          }
          catch (Exception ex)
          {
            x = null;
            res = false;
          }

          if (x != null)
          {
            res = true;
          }
          else
          {
            res = false;
           // Bot.SendTextMessageAsync(id, "Ошибка при передаче файла " + fi.Name);
            return res;
          }
      }
      else
      {
        return false;
      }
      
       //Bot.SendTextMessageAsync(id, "Передача файлов закончена.");
      return res;
    }

    private void sendLocationToPhone(string phone, string location, int msg_id = 0)
    {
      if (location == "") { return; }
      if (location.IndexOf("LA:") == -1 || location.IndexOf("LO:") == -1) { return; }
      
     

      try
      {

        if (_USERS == null)
        {
          return;
        }
        int id = getUserIdByPhone(phone);
        if (id > 0)
        {
          sendLocationToId(id, location);
        }
        else
        {
          if (SendNotification != null)
          {
            SendNotification(ResultCode.Message_AddressToError, msg_id);
          }
        }

      }
      catch (Exception ex)
      {

      }


    }

    private bool sendLocationTo(string dest, string location, bool toGroupe = false, int msg_id = 0 )
    {
      bool res = false;
      long id = 0;
      if (!toGroupe)
      {
        id = getUserIdByPhone(dest);
      }
      else
      {
        id = getGroupIdByName(dest);
      }
      
      if (id > 0)
      {
        res = sendLocationToId(id, location);
      }
      else
      {
        if (SendNotification != null)
        {
          SendNotification(ResultCode.Message_AddressToError, msg_id);
        }
      }
      return res;
    }
    
    private void sendLocationToGroup(string name, string location, int msg_id=0)
    {
      if (location == "") { return; }
      if (location.IndexOf("LA:") == -1 || location.IndexOf("LO:") == -1) { return; }
      try
      {

        if (_GROUPS == null)
        {
          return;
        }
        long id = getGroupIdByName(name);
        if (id > 0)
        {
          sendLocationToId(id, location);
        }
        else
        {
          if (SendNotification != null)
          {
            SendNotification(ResultCode.Message_AddressToError, msg_id);
          }
        }

      }
      catch (Exception ex)
      {

      }


    }
    
    private  bool sendLocationToId(long id,string location)
    {
      string[] _atts = location.Split(new char[] { ' ' });
      string slat = _atts[0].Substring(_atts[0].IndexOf("LAT:") + "LAT:".Length);
      string slon = _atts[1].Substring(_atts[0].IndexOf("LON:") + "LON:".Length);
      if (_atts.Length != 2) { return false; }
      try
      {
        float lat = float.Parse(slat);
        float lon = float.Parse(slon);
        if (Bot != null)
        {
          if (Bot.IsReceiving)
          {
            var x = Bot.SendLocationAsync(id, lat, lon);
            if (x != null)
            {
              return true;
            }
          }
        }
      }
      catch(Exception ex)
      {

      }

      return false;
    }
    
    private  bool sendMessageToPhone(string phone, string message , int msg_id=0)
    {
      if (_USERS == null)
      {
        return false;
      }
      int id = getUserIdByPhone(phone);
      if (id > 0)
      {
        return sendMessageToId(id, message);
      }
      else
      {
        if (SendNotification != null)
        {
          SendNotification(ResultCode.Message_AddressToError, msg_id);
        }
      }
      return false;
    }
    
    private bool sendMessageToGroup(string group, string message, int msg_id=0)
    {
      if (_GROUPS == null)
      {
        return false;
      }
      long id = getGroupIdByName(group);
      if (id > 0)
      {
        return sendMessageToId(id, message);
      }
      else
      {
        if (SendNotification != null)
        {
          SendNotification(ResultCode.Message_AddressToError, msg_id);
        }
      }
      return false;
    }

    private bool sendTextMessageTo(string dest, string text, bool sendtogroup = false, int msg_id =0)
    {
      bool res = false;
      long id = 0;
      if (!sendtogroup)
      {
        id = getUserIdByPhone(dest);
      }
      else
      {
        id = getGroupIdByName(dest);
      }
      if (id != 0)
      {
        res = sendMessageToId(id, text);
      }
      else
      {
        if (SendNotification != null)
        {
          SendNotification(ResultCode.Message_AddressToError, msg_id);
        }
      }
      return res;
    }
    
    private bool sendMessageToId(long id, string message)
    {

        if (Bot != null)
        {
          if (Bot.IsReceiving)
          {
            var x = Bot.SendTextMessageAsync(id, message);
            if (x != null)
            {
              return true;
            }
          }
        }
      
      return false;
    }

    private bool copyObject(string objectnumber, Int64 FromUserID, Int64 ToUserID = 0, int usernumber = 0)
    {
      bool res = false;

      return res;
    }

    private void timerWorker(Object arg)
    {


      lock (flags_Locker)
      {
        //DebugMessageEvent?.Invoke(Tracer.eLogLevel.INFO, "1/1");
        //Dictionary<int, byte> value = new Dictionary<int, byte>();
        //value.Add(65075, (byte)60);
        //try
        //{
        //  flags.Add(3333333333, value);
        //}
        //catch (Exception ex)
        //{

        //}


        var usersid = new List<Int64>(flags.Keys);
        foreach(var id in usersid)
        {
          if (flags[id].Count == 0)
          {
            flags.Remove(id);
            continue;
          }
          var objects = new List<uint>(flags[id].Keys);
          foreach(var obj in objects)
          {
            var val = (byte)(flags[id][obj]);
            if (val > 0)
            {
              flags[id][obj] =(byte) (val-1);
            }
            else if (val == 0)
            {
              flags[id].Remove(obj);
            }
          }
        }

 

        
        //DebugMessageEvent?.Invoke(Tracer.eLogLevel.INFO, "1/2");
      }
    }
    //=========================== Рабочий поток бота ==============================================

    private void WorkThread()
    { object current_message = null;
      WaitHandle[] handles = new WaitHandle[] { _ModuleThreadExitEvent, _MessageReceivedEvent };//WaitHandle[] handles = new WaitHandle[] { _ModuleThreadExitEvent, _MessageReceivedEvent, _SendMessageEvent };
      while (_RUN)
      {
        try
        {
          switch (WaitHandle.WaitAny(handles, Timeout.Infinite, true))
          {
            case 0:
              _RUN = false;
              if (Bot != null)
              {
                if (Bot.IsReceiving)
                {
                  try
                  {
                    //sendMessageToAll("Служба передачи сообщений через Telegram временно остановлена!");
                    sendToAdmins("Служба передачи сообщений через Telegram временно остановлена!");
                  }
                  catch (Exception ex)
                  {
                    _TRACER?.Write(Tracer.eLogLevel.DEBUG, ex.ToString());
                  }
                  try
                  {
                    Bot.StopReceiving();
                  }
                  catch (Exception ex)
                  {
                    _TRACER?.Write(Tracer.eLogLevel.DEBUG, ex.ToString());
                  }
                }
              }
              if (_TT != null)
              {

                _TT.Dispose();
                _TT = null;
              }
              
              break;
            case 1:
             // break;
            default:
              current_message = null;

              lock (_QUE)
              {
                if (_QUE.Count > 0)
                {
                  current_message = _QUE[0];
                  _QUE.RemoveAt(0);
                }
                else
                {
                  _MessageReceivedEvent.Reset();
                }

              }
              if (current_message != null)
              {
                if (current_message.GetType().Equals(typeof(ApiResponseEventArgs)))
                {

                }
                else if (current_message.GetType().Equals(typeof(MessageEventArgs)))
                {
                  BotMessageRecivedHandler((MessageEventArgs)current_message);
                }
                else if (current_message.GetType().Equals(typeof(CallbackQueryEventArgs)))
                {
                  CallBackQueryHandler((CallbackQueryEventArgs)current_message);
                }
                else if (current_message.GetType().Equals(typeof(TelegramDLL.Message)))
                {
                  ReciveMessageHandler((TelegramDLL.Message)current_message);
                }
                current_message = null;
              }
              break;
          }

        }
        catch(Exception ex)
        {
          _TRACER?.Write(Tracer.eLogLevel.DEBUG, ex.ToString());
        }
      }
    }

    private void BotMessageRecivedHandler(MessageEventArgs e)
    {
      string username = "";
      if (e.Message.From.IsBot)
      {
        return;
      }
      try
      {
        switch (e.Message.Type)
        {
          case TgBot.Types.Enums.MessageType.Text:
            if (!_USERS.ContainsKey("" + e.Message.From.Id))
            {
              if (e.Message.Text == "/start" || e.Message.Text.ToUpper() == "СТАРТ")
              {
                string uname = e.Message.From.Username;
                if (uname == null)
                {
                  uname = "(Заполните поле username (имя пользователя) в профиле)";
                }
                else if (uname == "")
                {
                  uname = "(Заполните поле username (имя пользователя) в профиле)";
                }
                List<KeyboardButton> keybs = new List<KeyboardButton>(0);
                KeyboardButton req = KeyboardButton.WithRequestContact("Передать свои данные");

                KeyboardButton cans = new KeyboardButton("Отмена");
                keybs.Add(req);
                keybs.Add(cans);
                var keyboard = new ReplyKeyboardMarkup(keybs.ToArray(), resizeKeyboard: true, oneTimeKeyboard: true);
                Bot.SendTextMessageAsync(e.Message.Chat.Id, "Уважаемый " + uname + "! Я вас не знаю. Отправьте мне свой контакт и я вас добавлю в список!!!", replyMarkup: keyboard);
                break;
              }
              else if (e.Message.Text.ToUpper() == "ОТМЕНА")
              {
                string txt = "Регистрация отменена пользователем.";
                List<KeyboardButton> keybs = new List<KeyboardButton>(0);
                KeyboardButton req = new KeyboardButton("Старт");
                keybs.Add(req);
                var keyboard = new ReplyKeyboardMarkup(keybs.ToArray(), resizeKeyboard: true, oneTimeKeyboard: true);
                keyboard.Selective = true;
                Bot.SendTextMessageAsync(e.Message.Chat.Id, txt, replyMarkup: keyboard);
                break;
              }
            }
            else
            {
              if (e.Message.Text == "/location")
              {
                List<KeyboardButton> keybs = new List<KeyboardButton>(0);
                KeyboardButton req = KeyboardButton.WithRequestLocation("Передать свои координаты");

                KeyboardButton cans = new KeyboardButton("Отмена");
                keybs.Add(req);
                keybs.Add(cans);
                var keyboard = new ReplyKeyboardMarkup(keybs.ToArray(), resizeKeyboard: true, oneTimeKeyboard: true);
                ReplyKeyboardRemove r = new ReplyKeyboardRemove();
                Bot.SendTextMessageAsync(e.Message.Chat.Id, "Очистка клавиатуры", replyMarkup: r);
                Bot.SendTextMessageAsync(e.Message.Chat.Id, "Ждём ваши координаты", replyMarkup: keyboard);
              }
              else if (e.Message.Text == "/getlocation")
              {
                Message msg = new Message();
                msg.Text = "Ha ha ha";
                msg.Location = new Location();
                msg.Location.Latitude = (float)56.84269;
                msg.Location.Longitude = (float)60.6894;
                Bot.SendLocationAsync(e.Message.Chat.Id, msg.Location.Latitude, msg.Location.Longitude);
              }
              else if (e.Message.Text == "/relusers")
              {
                try
                {
                  string r = (string)_USERS.SelectToken(String.Format("{0}.role", new object[] { e.Message.From.Id }));
                  if (r.Contains("admin") || r.Contains("super"))
                  if (r.Contains("admin") || r.Contains("super"))
                  {
                    if (LoadUsers())
                    {
                      Bot.SendTextMessageAsync(e.Message.Chat.Id, "Здравствуйте " + e.Message.From.Username + "! Список пользователей перезагружен.");
                    }
                    else
                    {
                      Bot.SendTextMessageAsync(e.Message.Chat.Id, "Здравствуйте " + e.Message.From.Username + "! Список пользователей не перезагружен!!! Ошибка при загрузке файла!");
                    }
                  }
                }
                catch (Exception ex)
                {
                  DebugMessageEvent?.Invoke(TelegramDLL.Tracer.eLogLevel.DEBUG, ex.ToString());
                }
                return;
              }
              else
              {
                if (e.Message.Text?.ToUpper() == "МЕНЮ")
                {
                  MenuItem mi = getMenuFor("");
                  Bot.SendTextMessageAsync(e.Message.Chat.Id, mi.Text, replyMarkup: mi.Keyboard);
                }
                else if (e.Message.Text.StartsWith("/"))
                {
                  if (e.Message.Text.Contains("start"))
                  {
                    Bot.SendTextMessageAsync(e.Message.Chat.Id, "Здравствуйте " + e.Message.From.Username.ToString() + "! Всё в порядке. Вы можете продолжать работу!", replyMarkup: getStartKeyboard());
                  }
                }
              }
            }

            break;
          case TgBot.Types.Enums.MessageType.Contact:
            username = ""+e.Message.From.Id;
            string fname = e.Message.Contact.FirstName;
            string lname = e.Message.Contact.LastName;
            string phone = e.Message.Contact.PhoneNumber;
            string cusername = e.Message.Contact.ToString();
            int id = e.Message.Contact.UserId;
            if (!_USERS.ContainsKey("" + e.Message.From.Id))
            {
              if (id == e.Message.From.Id)//если пользователь посылает свой контакт то работаем.
              {
                /*  if (fname == null || lname == null || phone == null || username == null)
                  {
                    Bot.SendTextMessageAsync(e.Message.Chat.Id, String.Format("Проверьте правильность заполнения своего профиля и попробуйте ещё раз!\r\nдолжны быть заполненны Фамилия, Имя и Username\r\nИмя: {0}\r\nФамилия: {1}\r\nUsername: {2}", new object[] { fname, lname, username }));
                    return;
                  }
                  else if (fname == "" || lname == "" || phone == "" || username == "")
                  {
                    Bot.SendTextMessageAsync(e.Message.Chat.Id, String.Format("Проверьте правильность заполнения своего профиля и попробуйте ещё раз!\r\nдолжны быть заполненны Фамилия, Имя и Username\r\nИмя: {0}\r\nФамилия: {1}\r\nUsername: {2}", new object[] { fname, lname, username }));
                    return;
                  }*/
                Newtonsoft.Json.Linq.JObject user = new Newtonsoft.Json.Linq.JObject();
                Newtonsoft.Json.Linq.JObject emptyobjects = new Newtonsoft.Json.Linq.JObject();
                user.Add("username", e.Message.From.Username.ToString());
                user.Add("firstname", fname);
                user.Add("lastname", lname);
                user.Add("phone", phone);
                user.Add("chatid", id);
                user.Add("role", "engineer");
                Newtonsoft.Json.Linq.JObject newUsers;
                if (_USERS == null)
                {
                  _USERS = new Newtonsoft.Json.Linq.JObject();
                }
                newUsers = _USERS;
                newUsers.Add("" + id, user);
                if (saveUsers(newUsers))
                {
                  _USERS = newUsers;
                  Bot.SendTextMessageAsync(e.Message.Chat.Id, "Вы успешно зарегистрированны!", replyMarkup: getStartKeyboard());
                  sendToAdmins("Зарегистрирован новый пользователь!!!\r\nФамилия: " + e.Message.Contact.FirstName + "\r\nИмя: " + e.Message.Contact.LastName + "\r\nНик: " + e.Message.From.Username + "\r\nТелефон: " + phone + "\r\nID: " + e.Message.Contact.UserId);
                }
                return;
              }
              else
              {
                if (((string)_USERS.SelectToken(username)["role"]).Contains("admin"))
                {
                  Bot.SendTextMessageAsync(e.Message.From.Id, "Пока что нельзя добавлять других пользователей!");
                }
                return;
              }
            }
            else if (id == e.Message.From.Id)
            {
              if ((string)_USERS.SelectToken("" + id)["username"] != e.Message.From.Username)
              {
                _USERS.SelectToken("" + id)["username"] = e.Message.From.Username;
              }
              if ((string)_USERS.SelectToken("" + id)["firstname"] != e.Message.From.FirstName)
              {
                _USERS.SelectToken("" + id)["firstname"] = e.Message.From.FirstName;
              }
              if ((string)_USERS.SelectToken("" + id)["lastname"] != e.Message.From.LastName)
              {
                _USERS.SelectToken("" + id)["lastname"] = e.Message.From.LastName;
              }
              Bot.SendTextMessageAsync(e.Message.From.Id, String.Format("Ваши данные обновлены!\r\nИмя: {0}\r\nФамилия: {1}, Username: {3}", new object[] { e.Message.From.LastName, e.Message.From.FirstName, e.Message.From.Username }));
              saveUsers();
            }
            break;
          case TgBot.Types.Enums.MessageType.Photo:
            break;
          case TgBot.Types.Enums.MessageType.Location:
            float lat = e.Message.Location.Latitude;
            float lon = e.Message.Location.Longitude;
            _TRACER?.Write(String.Format("Latitude: {0}\r\nLongitude: {1}\r\n", lat, lon));
            break;
          case TgBot.Types.Enums.MessageType.ChatMembersAdded:
            {
              Chat ch = e.Message.Chat;
              User[] us = e.Message.NewChatMembers;
              foreach (User user in us)
              {
                if (user.Id == Bot.BotId)
                {
                  addGroup(ch.Title, ch.Id);
                  sendToAdmins("Бот был добавлен в группу\r\nTitle: "+ch.Title+"\r\nDescription: "+ch.Description+"\r\nChat Id: "+ch.Id);
                }
              }
            }
            break;
          case TgBot.Types.Enums.MessageType.ChatMemberLeft:
            {
              Chat ch = e.Message.Chat;
              User us = e.Message.LeftChatMember;
              if (us.Id == Bot.BotId)
              {
                removeGroup(ch.Title);
                sendToAdmins("Бот был удалён из группы\r\nTitle: " + ch.Title + "\r\nDescription: " + ch.Description + "\r\nChat Id: " + ch.Id);
              }
            }

            break;
        }
      }
      catch (Exception ex)
      {
        _TRACER?.Write(Tracer.eLogLevel.DEBUG, ex.ToString());
      }


      return;
    }

    private void CallBackQueryHandler(CallbackQueryEventArgs e)
    {
      if (e.CallbackQuery.Data == "hide")
      {
        Bot.DeleteMessageAsync(e.CallbackQuery.Message.Chat.Id, e.CallbackQuery.Message.MessageId);
        return;
      }
      else if (e.CallbackQuery.Data == "backToMainMenu")
      {
        MenuItem mi = getMenuFor("");
        //Bot.EditMessageTextAsync(e.CallbackQuery.Message.Chat.Id, e.CallbackQuery.Message.MessageId, "Выберите пункт", replyMarkup: getMainMenu());//, replyMarkup: null
        Bot.EditMessageTextAsync(e.CallbackQuery.Message.Chat.Id, e.CallbackQuery.Message.MessageId, mi.Text, replyMarkup: mi.Keyboard);
        return;
      }
      else if (e.CallbackQuery.Data.StartsWith("GoTo"))
      {
        string path = e.CallbackQuery.Data.Substring(5).Trim();
        MenuItem mi = getMenuFor(path);

        Bot.EditMessageTextAsync(e.CallbackQuery.Message.Chat.Id, e.CallbackQuery.Message.MessageId, mi.Text, replyMarkup: mi.Keyboard);//, replyMarkup: null
      }
      else if (e.CallbackQuery.Data.StartsWith("{"))
      {
        try
        {
          JObject d = JObject.Parse(e.CallbackQuery.Data.Replace('\\', '/'));
          {
            string cmd = d.First.Path;

            if (cmd == "GoTo")
            {
              string url = (string)((JProperty)d.First).Value;
              Bot.EditMessageTextAsync(e.CallbackQuery.Message.Chat.Id, e.CallbackQuery.Message.MessageId, "Выберите пункт", replyMarkup: getNextMenu(url));//, replyMarkup: null
              return;
            }
            else if (cmd == "GetDoc")
            {
              string ss = "Выберите вид документации для:\r\n" + (string)(d.SelectToken("GetDoc")) + "\r\n";
              string path = getPathForNums((string)(d.SelectToken("GetDoc")));
              MenuItem m = getMenuFor((string)(d.SelectToken("GetDoc")));
              Bot.EditMessageTextAsync(e.CallbackQuery.Message.Chat.Id, e.CallbackQuery.Message.MessageId, m.Text, replyMarkup: m.Keyboard);

            }
            else if (cmd == "GetFile")
            {
              string path = (string)(d.SelectToken("GetFile"));
              FileItem fi = getDocFileFor(path);

              FileStream fs = new FileStream(fi.Path, FileMode.Open);

              InputOnlineFile f = new InputOnlineFile(fs);
              f.FileName = fi.Name;
              Bot.DeleteMessageAsync(e.CallbackQuery.Message.Chat.Id, e.CallbackQuery.Message.MessageId);
              Bot.SendDocumentAsync(e.CallbackQuery.Message.Chat.Id, f);
            }
          }
        }
        catch (Exception ex)
        {

        }
        return;
      }
      return;
    }

    private void sendToAdmins(string message)
    {
      if(Bot == null) { return; }
      if (!Bot.IsReceiving) { return; }
      try
      {
        foreach (Newtonsoft.Json.Linq.JToken user1 in _USERS.Children())
        {
          if (_USERS.SelectToken(user1.Path + ".role") != null)
          {
            if (((string)_USERS.SelectToken(user1.Path)["role"]).Contains("admin"))
            {
              Task<Message> ts = Bot.SendTextMessageAsync((Int64)_USERS.SelectToken(user1.Path)["chatid"], message);
              try
              {
                ts.Wait(2000);
              }catch(Exception ex)
              {
                _TRACER?.Write(Tracer.eLogLevel.DEBUG, ex.ToString());
              }
            }
          }
        }
      }
      catch(Exception ex)
      {
        _TRACER?.Write(Tracer.eLogLevel.DEBUG, ex.ToString());
      }

    }

    //=========================== Отправка отладочного сообщения ==================================
    public bool GreaterOrEqualLogLevel(Tracer.eLogLevel logLevel)
    {
      return _TRACER.GreaterOrEqualLogLevel(logLevel);
    }
    public bool EqualLogLevel(Tracer.eLogLevel logLevel)
    {
      return _TRACER.EqualLogLevel(logLevel);
    }

    private void ModuleThread_DebugMessage(Tracer.eLogLevel logLevel, string _text)
    {
      if (GreaterOrEqualLogLevel(logLevel))
      {
        ModuleThread_DebugMessage(_text);
      }
    }
    private void ModuleThread_DebugMessage(string _text)
    {
      _TRACER?.Write(_text);
     /* Message _msg = new Message(MessageType.DEBUG_DRV_TLG);
      _msg.ModuleId = ModuleId;
      _msg.Set("Text", _text);
      _msg.TimeStamp = DateTime.Now;
      ReceiveMessage(this, _msg);*/
    }
    private void ModuleThread_DebugMessage(string text, byte[] src, int offset, int len)
    {
      string _res = text;
      for (int i = 0; i < len; i++)
        _res += string.Format("0x{0:X2} ", src[offset + i]);
      _TRACER?.Write(_res);
     /* Message _msg = new Message(MessageType.DEBUG_DRV_TLG);
      _msg.ModuleId = ModuleId;
      _msg.Content = new byte[len];

      for (int i = 0; i < len; i++)
        _msg.Content[i] = src[offset + i];

      _msg.TimeStamp = DateTime.Now;
      _msg.Set("Text", text);
      ReceiveMessage(this, _msg);*/
    }

  }
}
