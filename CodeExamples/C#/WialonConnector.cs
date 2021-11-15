using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using System.Net.Http;
/*
Код ошибки	       Значение
0	                Удачное выполнение операции (например при логауте это будет успешный выход)
1                 Недействительная сессия
2                	Неверное имя сервиса
3	                Неверный результат
4	                Неверный ввод
5	                Ошибка выполнения запроса
6	                Неизвестная ошибка
7	                Доступ запрещен
8	                Неверный пароль или имя пользователя
9	                Сервер авторизации недоступен, пожалуйста попробуйте повторить запрос позже
10                Превышен лимит одновременных запросов
11                Ошибка во время выполнения запроса на сброс пароля
14                Ошибка биллинга
1001              Нет сообщений для выбранного интервала
1002              Элемент с таким уникальным свойством уже существует или Невозможно создать элемент в связи с ограничениями биллинга
1003              1 - Только один запрос разрешается в данный момент времени
1003              2 - превышено кол-во API запросов/«reason»:«LIMIT api_concurrent»
1003              3 превышено кол-во слоев /«reason»:«LAYERS_MAX_COUNT»
1003              4 - превышен лимит сессий/«reason»:«NO_SESSION»
1003              5 - временно недоступна база /«reason»:«LOCKER_ERROR»
1004              Превышено ограничение по числу сообщений
1005              Ограничение по времени выполнения было превышено
1006              Превышение лимита попыток ввода кода двухфакторной авторизации
1011              Время сессии истекло либо ваш IP изменился
2006              Учетная запись не может быть изменена
2008              Нет прав пользователя на объект (при изменении учетной записи)
2014              Текущий пользователь не может быть выбран при создании учетной записи
2015              Удаление датчика запрещено по причине использования в другом датчике или дополнительных свойствах объекта
*/
namespace Module2Connect.WialonLayer
{
   class WialonConnector
   {

      private Thread _WORK_THREAD;
      private string _TOKEN;
      private string _SESSION_ID;
      private bool _CONNECTED;
      private HttpClient _HTTP_CLIENT;
      private ManualResetEvent _SEND_CHECK_CONNECT;
      //private Task _CHECK_CONNECT_TASK;
      private Newtonsoft.Json.Linq.JArray _NEW_WIALON_EVENTS;
      private ManualResetEvent _NEW_EVENTS_PRESENT;
      private const string _BASE_URL = "hst-api.wialon.com";
      private List<Newtonsoft.Json.Linq.JObject> _EVENTS_QUEUE;
      private JArray _Cars;
      private JArray _GeoZones;
      private int _RootId;
      private long _UserId;
      private int _SecUserId;
      private List<GeozoneGroup> _GEOZONEGROUPS;

      public JArray Cars
      {
         get
         {
            return this._Cars;
         }
         set
         {
            this._Cars = value;
         }
      }

      public JArray Geozones
      {
         get
         {
            return this._GeoZones;
         }
         set
         {
            this._GeoZones = value;
         }
      }

      public int RootId
      {
         get
         {
            return this._RootId;
         }
      }

      public long UserId
      {
         get => _UserId;
         set
         {
            this._UserId = value;
         }
      }
      public int SecondUserId
      {
         get => _SecUserId;
         set
         {
            this._SecUserId = value;
         }
      }
      public string Token
      {
         get
         {
            return _TOKEN;
         }
         set
         {
            _TOKEN = value;
         }
      }

      public string Sid
      {
         get
         {
            return _SESSION_ID;
         }
         set
         {
            _SESSION_ID = value;
         }
      }

      public WialonConnector()
      {
         this._CONNECTED = false;
         this._HTTP_CLIENT = new HttpClient();
         this._SEND_CHECK_CONNECT = new ManualResetEvent(true);
         this._NEW_EVENTS_PRESENT = new ManualResetEvent(false);
         this._EVENTS_QUEUE = new List<JObject>(0);
         this._GEOZONEGROUPS = new List<GeozoneGroup>();


      }

      public WialonConnector(string token)
      {
         this._TOKEN = token;
         this._HTTP_CLIENT = new HttpClient();
         this._SEND_CHECK_CONNECT = new ManualResetEvent(true);
         this._NEW_EVENTS_PRESENT = new ManualResetEvent(false);
         this._EVENTS_QUEUE = new List<JObject>(0);
         this._GEOZONEGROUPS = new List<GeozoneGroup>();
      }

      public WialonConnector(string token, string sid)
      {
         this._TOKEN = token;
         this._SESSION_ID = sid;
         this._HTTP_CLIENT = new HttpClient();
         this._SEND_CHECK_CONNECT = new ManualResetEvent(true);
         this._NEW_EVENTS_PRESENT = new ManualResetEvent(false);
         this._EVENTS_QUEUE = new List<JObject>(0);
         this._GEOZONEGROUPS = new List<GeozoneGroup>();
      }

      //private void CheckTask()
      //{
      //  _SEND_CHECK_CONNECT.Set();
      //}



      private string POST(string url, string command, string data)
      {

         string res = "";
         string sid = "sid={0}&";
         if (command.CompareTo("token/login") == 0 /*|| command.CompareTo("core/update_data_flags")==0*/)
         {
            sid = "";
         }
         else
         {
            sid = String.Format(sid, _SESSION_ID);
         }
         string text = "{0}svc={1}&params={2}";
         string address = "";
         if (String.IsNullOrEmpty(url))
         {
            address = String.Format("http://{0}/wialon/ajax.html", _BASE_URL); //http://hst-api.wialon.com/wialon/ajax.html
         }
         else
         {
            address = url;
         }

         string txtcontetnt = String.Format(text, sid, command, data);
         var content = new StringContent(txtcontetnt, Encoding.UTF8, "application/x-www-form-urlencoded");
         var r = _HTTP_CLIENT.PostAsync(address, content).Result;

         var ctype = r.Content.Headers.ContentType;
         var sres = r.Content.ReadAsStringAsync();
         string ssres = sres.Result.ToString();
         res = ssres;
         return res;
      }

      private string GET(string url, string command, string data)
      {
         //string uri = String.Format("https://{0}/wialon/ajax.html?svc=core/search_items&params={1}&sid={2}", _BASE_URL, parametres.ToString(Newtonsoft.Json.Formatting.None), _SESSION_ID);
         string res = "";
         if (String.IsNullOrEmpty(url))
         {
            url = String.Format("http://{0}/wialon/ajax.html", _BASE_URL);
         }
         string par = "&params={0}";
         if (String.IsNullOrEmpty(data))
         {
            par = "";

         }
         else
         {
            par = String.Format(par, data);
         }
         string cmd = "?svc={0}";
         if (String.IsNullOrEmpty(command))
         {
            cmd = "";
         }
         else
         {
            cmd = String.Format(cmd, command);
         }
         string sid = "&sid={0}";
         if (String.IsNullOrEmpty(par) && String.IsNullOrEmpty(cmd))
         {
            sid = String.Format("?sid={0}", _SESSION_ID);
         }
         else
         {
            sid = String.Format(sid, _SESSION_ID);
         }

         string endurl = String.Format("{0}{1}{2}{3}", url, cmd, par, sid);
         var result = _HTTP_CLIENT.GetAsync(endurl);//_HTTP_CLIENT.PostAsync(address, content).Result;
         result.Wait();
         var r = result.Result;
         var ctype = r.Content.Headers.ContentType;
         var sres = r.Content.ReadAsStringAsync();
         string ssres = sres.Result.ToString();
         if (!String.IsNullOrEmpty(ssres))
         {
            res = ssres;
         }
         return res;
      }

      public string GetToken(string login, string pass)
      {
         string res = "";

         return res;
      }

      public bool Login()
      {
         bool res = false;
         if (String.IsNullOrEmpty(_TOKEN))
         {
            throw new MissingFieldException("Token is null or empty!");
         }
         JObject prms = new JObject();
         prms.Add("token", _TOKEN);
         prms.Add("fl", 14);
         string txtparams = prms.ToString(Newtonsoft.Json.Formatting.None);
         string cmd = Commands.Login;//"token/login";
                                            //var text = "svc=token/login&params={\"token\":\"" + _TOKEN + "\",\"fl\":4}";
                                            //var content = new StringContent(text, Encoding.UTF8, "application/x-www-form-urlencoded");
                                            //var address = "https://hst-api.wialon.com/wialon/ajax.html";

         //var result = _HTTP_CLIENT.PostAsync(address, content).Result;
         //var ctype = result.Content.Headers.ContentType;
         //var sres = result.Content.ReadAsStringAsync();
         //string ssres = sres.Result.ToString();
         string ssres = POST(null, cmd, txtparams);
         // var hres = result.Headers.ToString();

         // if (ctype.MediaType == "application/json")
         {
            Newtonsoft.Json.Linq.JObject contentdata = JsonConvert.DeserializeObject<Newtonsoft.Json.Linq.JObject>(ssres);

            if (contentdata.ContainsKey("tm"))
            {
               string tm = contentdata.GetValue("tm").ToString();
               Console.WriteLine("Connetion time: \t" + tm);
            }

            if (contentdata.ContainsKey("user"))
            {
               UserId = (long)contentdata["user"]["id"];

              SecondUserId = (int)contentdata["user"]["ld"];
            }

            if (contentdata.ContainsKey("au"))
            {
               string user = contentdata.GetValue("au").ToString();
               Console.WriteLine("Connected as \t" + user);
            }
            if (contentdata.ContainsKey("gis_sid"))//eid gis_sid
            {
               string gis_sid = contentdata.GetValue("gis_sid").ToString();
               Console.WriteLine("Gis_Sid: \t" + gis_sid);
            }

            if (contentdata.ContainsKey("eid"))//eid gis_sid
            {
               string eid = contentdata.GetValue("eid").ToString();
               Console.WriteLine("EID: \t" + eid);
               if (String.IsNullOrEmpty(_SESSION_ID) || _SESSION_ID != eid)
               {
                  _SESSION_ID = eid;
               }
               res = true;
               _CONNECTED = true;
            }
         }

         //res = (result.StatusCode == System.Net.HttpStatusCode.OK ? true : false);
         //_CONNECTED = res;
         return res;

      }

      public bool Logout()
      {

         bool res = false;
         if (!_CONNECTED)
         {
            return true;
         }
         string cmd = Commands.Logout;//"core/logout";
         string prm = "{}";
         //var text = String.Format("svc=core/logout&params={0}&sid={1}", "{}", _SESSION_ID);
         //string uri = String.Format("https://{0}/wialon/ajax.html", _BASE_URL);
         //var content = new StringContent(text, Encoding.UTF8, "application/x-www-form-urlencoded");
         //var result = _HTTP_CLIENT.PostAsync(uri, content).Result;
         //var ctype = result.Content.Headers.ContentType;
         //var sres = result.Content.ReadAsStringAsync();
         //string ssres = sres.Result.ToString();
         string ssres = POST(null, cmd, prm);
         JObject contentdata = JsonConvert.DeserializeObject<JObject>(ssres);
         if (contentdata.ContainsKey("error"))
         {
            switch ((int)contentdata["error"])
            {
               case 0:
                  res = true;
                  _CONNECTED = false;
                  _SESSION_ID = "";
                  break;
               case 1:
                  break;
               case 2:
                  break;
               case 3:
                  break;
               case 4:
                  res = true;
                  _CONNECTED = false;
                  _SESSION_ID = "";
                  break;
            }
         }
         return res;
      }

      public JArray AddObjectToObserver(JObject value)
      {
         return this.AddObjectToObserver(GetObjectId(value));
      }

      public JArray AddObjectToObserver(int id)
      {
         string command = Commands.UpdateFlags;// "core/update_data_flags";
                                                      //http://{host}/wialon/ajax.html?sid=<text>&svc=<svc>&params={<params>}
         string url = "https://{0}/wialon/ajax.html?sid={2}&svc=core/update_data_flags&params={1}";

         string text = "sid={0}&svc={1}&params={2}";
         string address = String.Format("http://{0}/wialon/ajax.html", _BASE_URL);

         JObject parametres = new JObject();
         JArray specs = new JArray();
         JObject items = new JObject();
         items.Add("type", "id");
         items.Add("data", (long)id);
         items.Add("flags", (uint)1024);
         items.Add("mode", (uint)0);
         //items.Add("max_items", (uint)1);

         specs.Add(items);
         parametres.Add("spec", specs);

         string data = parametres.ToString(Newtonsoft.Json.Formatting.None);

         string ssres = POST(null, command, data);
         //Console.WriteLine("Data: " + data+"\r\nAnswer: "+ssres);
         //string uri = String.Format(url, _BASE_URL, data,_SESSION_ID);
         //string txtcontetnt = String.Format(text, _SESSION_ID, command, data);
         //var content = new StringContent(txtcontetnt, Encoding.UTF8, "application/x-www-form-urlencoded");
         ////var address = String.Format("https://{0}/wialon/ajax.html", _BASE_URL);
         //Console.WriteLine("Address: "+ address);
         //Console.WriteLine("Content: " + txtcontetnt);
         //var r = _HTTP_CLIENT.PostAsync(address, content).Result;

         ////var result = _HTTP_CLIENT.GetAsync(uri);//_HTTP_CLIENT.PostAsync(address, content).Result;
         ////result.Wait();
         ////var r = result.Result;
         //var ctype = r.Content.Headers.ContentType;
         //var sres = r.Content.ReadAsStringAsync();
         //string ssres = sres.Result.ToString();
         //Console.WriteLine("Add obj: " + ssres);
         return JsonConvert.DeserializeObject<Newtonsoft.Json.Linq.JArray>(ssres);
      }

      public string AddObjectToSession(JObject item)
      {
         string res = "";
         int id = GetObjectId(item);
         res = AddObjectToSession(id);
         return res;
      }

      public string AddObjectToSession(string name)
      {
         string res = "";
         JObject obj = SearchByName(name);
         int id = GetObjectId(name);
         res = AddObjectToSession(id);
         return res;
      }

      public string AddObjectToSession(int id)
      {
         /*
             svc=events/update_units&params={"mode":"add",
                         "units":[
                                 {
                                 "id":<long>,
                                 "detect":
                                     {
                                         "trips":<uint>,
                                         "lls":<uint>,
                                         "sensors":<uint>,
                                         "ignition":<uint>,
                                         "counters":<uint>,
                 "speedings":<uint>,

                                     }
                                 },
                                 ...
                         ]}
         */
         string command = Commands.UdateUnits;// "events/update_units";
         JObject parameters = new JObject();
         JArray units = new JArray();
         JObject unit = new JObject();
         JObject detect = new JObject();
         detect.Add("*", 0);
         unit.Add("id", (uint)id);
         unit.Add("detect", detect);
         units.Add(unit);
         parameters.Add("mode", "add");
         parameters.Add("units", units);


         string data = parameters.ToString(Newtonsoft.Json.Formatting.None);
         string ssres = POST(null, command, data);
         //Console.WriteLine("Data: " + data);
         ////string uri = String.Format(url, _BASE_URL, data, _SESSION_ID);
         //string txtcontetnt = String.Format(text, _SESSION_ID, command, data);
         //var content = new StringContent(txtcontetnt, Encoding.UTF8, "application/x-www-form-urlencoded");
         ////var address = String.Format("https://{0}/wialon/ajax.html", _BASE_URL);
         //Console.WriteLine("Address: " + address);
         //Console.WriteLine("Content: " + txtcontetnt);
         //var r = _HTTP_CLIENT.PostAsync(address, content).Result;

         ////var result = _HTTP_CLIENT.GetAsync(uri);//_HTTP_CLIENT.PostAsync(address, content).Result;
         ////result.Wait();
         ////var r = result.Result;
         //var ctype = r.Content.Headers.ContentType;
         //var sres = r.Content.ReadAsStringAsync();
         //string ssres = sres.Result.ToString();
         Console.WriteLine(String.Format("Add object {0} to session: {1}", id, ssres.Trim()));
         Newtonsoft.Json.Linq.JObject jobj = JsonConvert.DeserializeObject<Newtonsoft.Json.Linq.JObject>(ssres);
         return ssres;

      }

      private void GetNewEventsByWialon(object value)
      {
         //avl_evts?sid=<text>
         //string text = "avl_evts?sid=" + _SESSION_ID + "";
         //StringContent content = new StringContent(text, Encoding.UTF8, "application/x-www-form-urlencoded");
         ////var address = "https://hst-api.wialon.com/wialon/ajax.html";
         //string uri = String.Format("https://{0}/avl_evts?sid={1}",_BASE_URL,_SESSION_ID);
         //// var result = _HTTP_CLIENT.PostAsync(address, content).Result;
         //Task<HttpResponseMessage> result = _HTTP_CLIENT.GetAsync(uri);
         //result.Wait();
         //HttpResponseMessage r = result.Result;
         //var ctype = r.Content.Headers.ContentType;
         //var sres = r.Content.ReadAsStringAsync();
         ////string ssres = sres.Result.ToString();


         string command = "avl_evts";
         string url = String.Format("http://{0}/{1}", _BASE_URL, command);
         string ssres = GET(url, "", "");
         //Console.WriteLine("New Event from vialon: " + ssres);
         Newtonsoft.Json.Linq.JObject jResault = JsonConvert.DeserializeObject<Newtonsoft.Json.Linq.JObject>(ssres);
         Newtonsoft.Json.Linq.JArray events = (Newtonsoft.Json.Linq.JArray)jResault["events"];
         if (events?.Count > 0)
         {
            foreach (JObject ev in events)
            {
               _EVENTS_QUEUE.Add(ev);
            }

            this._NEW_EVENTS_PRESENT.Set();
         }

      }

      public Newtonsoft.Json.Linq.JArray GetObjectsFromFialon()
      {//name in json answer - items
         string command = "core/search_items";
         Newtonsoft.Json.Linq.JArray jresult = new Newtonsoft.Json.Linq.JArray();
         var text = "svc=core/search_items&params={ \"spec\":{ \"itemsType\":\"avl_unit\",\"propName\":\"sys_name\",\"propValueMask\":\" * \",\"sortType\":\"sys_name\"},\"force\":1,\"flags\":\"0x00000001\",\"from\":0,\"to\":0}&sid=" + _SESSION_ID;
         var arr = new Newtonsoft.Json.Linq.JObject();
         var spec = new Newtonsoft.Json.Linq.JObject();
         spec.Add("itemsType", "avl_unit");
         spec.Add("propName", "sys_name");
         spec.Add("propValueMask", "*");
         spec.Add("sortType", "sys_name");
         arr.Add("spec", spec);
         arr.Add("force", 1);
         arr.Add("flags", 0x00000001);
         arr.Add("from", 0);
         arr.Add("to", 0);

         string ssres = GET(null, command, arr.ToString(Newtonsoft.Json.Formatting.None));
         Newtonsoft.Json.Linq.JObject jobj = JsonConvert.DeserializeObject<Newtonsoft.Json.Linq.JObject>(ssres);
         jresult = (Newtonsoft.Json.Linq.JArray)jobj.GetValue("items");
         return jresult;
      }

      public bool FillCars()
      {
         bool res = false;
         JArray cars = GetObjectsFromFialon();
         if (cars.Count > 0)
         {
            this._Cars = cars;
            res = true;
         }
         return res;
      }

      public bool FillGeozones()
      {
         bool res = false;
         
         JObject zonesdata = GetAlZonesFromWialon();
         if (zonesdata != null && zonesdata.Count == 2)
         {
            this._GeoZones = (JArray)zonesdata?["zones"];
            this._RootId = (int)zonesdata?["id"];
            List<JToken> t = _GeoZones.Where(z => ((string)z["n"]) == "Шишимская горка,Блок 15").Select(z => z).ToList();
            res = true;
         }



         return res;
      }
      public int GetObjectId(string name)
      {
         int res = -1;
         JObject item = SearchByName(name);
         if (item != null)
         {
            res = GetObjectId(item);
         }
         return res;
      }

      public int GetObjectId(JObject value)
      {
         int idx = -1;
         if (value.ContainsKey("id"))
         {
            idx = (int)value["id"];
         }
         return idx;
      }

      public JArray CheckGeozonesFor(JObject value, long id = 0)
      {
         JArray resault = new JArray();
         // svc = resource / get_zones_by_point &params={
         //   "spec": {
         //     "lat":< double >,
         //    "lon":< double >,
         //    "radius":< double >,
         //    "zoneId":{
         //       "<long>":[< uint >,
         //        ...
         //	],
         //    ...
         //    },
         //}
         // }
         JObject data = new JObject();
         var car = SearchByName((string)value["nm"]);
         string cmd = Commands.GetGeozonesFor;
         JObject parms = new JObject();
         JObject spec = new JObject();
         spec.Add("lat", car?["pos"]["y"]);//y
         spec.Add("lon", car?["pos"]["x"]);//x
         spec.Add("radius", 100);

         JObject ziid = new JObject();
         JArray ids = new JArray();
         ziid.Add(String.Format("{0}", id), ids);
         spec.Add("zoneId", ziid);
         data.Add("spec", spec);
         string r = POST(null, cmd, data.ToString(Newtonsoft.Json.Formatting.None));
         var rr = Newtonsoft.Json.JsonConvert.DeserializeObject(r);
         var edata = ((JObject)rr)[String.Format("{0}", id)];
         if (((JObject)rr).Count > 0)
         {
            foreach (JProperty zid in edata)
            {
               resault.Add(zid.Name);
            }
         }
         return resault;
      }

      public JArray CheckGeozonesFor(JObject value)
      {
         return CheckGeozonesFor(value, this._RootId);
      }

      public JObject SearchByName(string name)
      {
         string command = "core/search_items";
         string example = "";
         JArray items;
         JObject item = null;
         string tt = $"{ItemsType.AVL_UNIT}";//*Volvo*

         example = "{\"spec\":{\"itemsType\":\"avl_unit\",\"propName\":\"sys_name\",\"propValueMask\":\"*Volvo*\",\"sortType\":\"sys_name\"},\"force\":1,\"flags\":1439,\"from\":0,\"to\":0}";
         //      hst - api.wialon.com/wialon/ajax.html?svc=core/search_items&params={"spec"{"itemsType":"avl_unit","propName":"sys_name","propValueMask":"*Volvo*","sortType":"sys_name"},"force":1,"flags":1439,"from":0,"to":0}&sid = 

         JObject parametres = JsonConvert.DeserializeObject<JObject>(example);
         ((JObject)parametres["spec"])["propValueMask"] = name;
         ((JObject)parametres["spec"])["itemsType"] = tt;
         //string uri = String.Format("https://{0}/wialon/ajax.html?svc=core/search_items&params={1}&sid={2}", _BASE_URL, parametres.ToString(Newtonsoft.Json.Formatting.None),_SESSION_ID);
         //var result = _HTTP_CLIENT.GetAsync(uri);//_HTTP_CLIENT.PostAsync(address, content).Result;
         //result.Wait();
         //var r = result.Result;
         //var ctype = r.Content.Headers.ContentType;
         //var sres = r.Content.ReadAsStringAsync();
         //string ssres = sres.Result.ToString();
         string ssres = GET(null, command, parametres.ToString(Newtonsoft.Json.Formatting.None));
         Newtonsoft.Json.Linq.JObject jobj = JsonConvert.DeserializeObject<Newtonsoft.Json.Linq.JObject>(ssres);
         if (jobj.ContainsKey("items"))
         {
            items = (JArray)jobj["items"];
            if (items.Count == 1)
            {
               item = (JObject)items.First();
            }
         }

         return item;
      }

      public string GetCarNameById(JArray cars, int id)
      {
         if(cars == null) { return ""; }
         string res = "";
         var t = cars.Where(i => i["id"].Value<int>() == id).Select(s => (JToken)s["nm"]).ToList();
         if (t.Count == 1)
         {
            res = t.First().Value<string>();
         }
         return res;
      }

      public string GetCarNameById(int id)
      {
         string res = "";
         res = GetCarNameById(this._Cars, id);
         return res;
      }

      public JObject GetAlZonesFromWialon()
      {

         /*
         svc=core/search_items&params={"spec":{
                         "itemsType":<text>,	
                         "propName":<text>,	
                         "propValueMask":<text>,	
                         "sortType":<text>,
                         "propType":<text>,
                         "or_logic":<bool>	
                          },
                          "force":<uint>,			
                          "flags":<long>,			
                       "from":<uint>,			
                          "to":<uint>} 
          */
         JObject resault = new JObject();
         JArray zones_array = new JArray();
         string command = Commands.Search;//"core/search_items";
         JObject data = new JObject();
         JObject spec = new JObject();
         spec.Add("itemsType", ItemsType.AVL_RESOURCE);//"avl_resource"
         spec.Add("propName", "");
         spec.Add("propValueMask", "");
         spec.Add("sortType", "");
         spec.Add("propType", PropType.None);
         spec.Add("or_logic", true);

         data.Add("spec", spec);
         data.Add("force", 1);
         long fl = FLAGS.GeoZones|FLAGS.BaseFlag |FLAGS.GeoZoneGroups |FLAGS.ReportsTemplates | FLAGS.Trucks |FLAGS.Tasks | FLAGS.Requests | FLAGS.POI | FLAGS.Notifications;
         data.Add("flags", fl);
         data.Add("from", 0);
         data.Add("to", 0);
         string ssres = GET(null, command, data.ToString(Newtonsoft.Json.Formatting.None));
         // Console.WriteLine(ssres);
         Newtonsoft.Json.Linq.JObject jobj = JsonConvert.DeserializeObject<Newtonsoft.Json.Linq.JObject>(ssres);
         if (jobj.ContainsKey("items"))
         {

            JArray tmp = (JArray)jobj["items"];
            foreach (JObject item in tmp)
            {

               if (item.ContainsKey("zl"))
               {
                  var id = item["id"];
                  resault.Add("id", id);
                  JObject zones = (JObject)(((JObject)item)["zl"]);
                  foreach (var zone in zones)
                  {
                     // Console.WriteLine(zone.Value);
                     zones_array.Add(zone.Value);
                     //Console.WriteLine( "Зона "+zone.Value["id"]+": "+ zone.Value["n"]);              
                  }
                  resault.Add("zones", zones_array);
               }

               if (item.ContainsKey("zg"))
               {
                  JObject groups = (JObject)item["zg"];
                  foreach (var group in groups)
                  {
                     string id = group.Key;
                     JObject value = (JObject)group.Value;
                     GeozoneGroup g = new GeozoneGroup();
                     g.Id = (uint)value["id"];
                     g.Name = (string)value["n"];
                     g.Description = (string)value["d"];
                     JArray zonesIds = (JArray)value["zns"];
                     foreach(var zid in zonesIds)
                     {
                        g.Zones.Add((uint)zid);
                     }
                     g.Zones.Sort();
                     List<GeozoneGroup> upload = _GEOZONEGROUPS.Where(gupload => gupload.Name == g.Name).Select(gitem => gitem).ToList();
                     //if (upload != null)
                     //{
                     //   if (upload.Count >= 1)
                     //   {
                     //      if(upload.)
                     //   }
                     //}
                     if(upload.Count == 0)
                     {
                        _GEOZONEGROUPS.Add(g);
                     }
                     else if(upload.Count==1)
                     {
                        if (_GEOZONEGROUPS.Remove(upload[0]))
                        {
                           _GEOZONEGROUPS.Add(g);
                        }
                     }
                     else
                     {
                        Console.WriteLine("Ошибка!!!!! оШИБКА!!!");
                     }
                     
                  }
               }

            }
         }
         return resault;
      }

      public bool geozoneExists(JObject geozone)
      {
         bool res = false;
         if (_GeoZones.Where(z => ((string)z["n"]) == (string)geozone["name"]).Select(z => z).ToList().Count > 0)
         {
            return true;
         }
         else
         if (_GeoZones.Where(z => (string)z["b"]["cen_x"] == (string)geozone["x"] && (string)z["b"]["cen_y"] == (string)geozone["y"]).Select(z => z).ToList().Count > 0)
         {
            return true;
         }
         
         return res;
      }

      public JArray AddGeoZoneToUnloadingGroup(JObject zone)
      {
         /*
          * 
          "[91,{\"n\":\"Тестовая геозона\",\"d\":\"Учимся создавать геозоны\",\"id\":91,\"f\":112,\"t\":3,\"w\":100,\"e\":51597,\"c\":11141282,\"i\":4294967295,\"libId\":0,\"path\":\"\",\"b\":{\"min_x\":61.3214826256,\"min_y\":56.8163322867,\"max_x\":61.3247576088,\"max_y\":56.8181281625,\"cen_x\":61.3231201172,\"cen_y\":56.8172302246},\"ct\":1634128677,\"mt\":1634128677}]\n"* 
         svc=resource/update_zone&params={"itemId":<long>,
				          "id":<long>,
				          "callMode":<text>,
				          "n":<text>,
				          "d":<text>,
				          "t":<int>,
				          "w":<int>,
				          "f":<uint>,
				          "c":<int>,
				          "tc":<uint>,
				          "ts":<uint>,
				          "min":<uint>,
				          "max":<uint>,
				          "path":<text>,
				          "libId":<long>,
				          "oldItemId":<uint>,
				          "oldZoneId":<uint>,
				          "jp":<JSON>,
				          "p":[				
					         {
						         "x":<double>,
						         "y":<double>,
						         "r":<int>
					         }
				          ]} 
         "{\"error\":4, \"reason\":\"VALIDATE_PARAMS_ERROR: {n: text, t: uint, w: uint, f: uint, c: uint, p: [{x: double, y: double, r: uint}]}\"}\n"
          */

         JArray res = new JArray();
         try
         {
            JObject data = new JObject();
            string cmd = Commands.UpdateGeozonesGroup;
            data.Add(new JProperty("itemId", (long)_RootId));
            data.Add(new JProperty("id", (long)3)); //id геозоны. При создании = 0
            data.Add(new JProperty("callMode", "update")); // режим вызова - создать
                                                           //data.Add(new JProperty("t", (uint)zone["t"])); // тип: 1 - линия, 2 - полигон, 3 - круг
            data.Add(new JProperty("n", "Зоны выгрузки")); // имя гозоны
            data.Add(new JProperty("d", "")); // описание геозоны
                                              //data.Add(new JProperty("w", (uint)100));
                                              //data.Add(new JProperty("c", (uint)0xAA00A2));
                                              //data.Add(new JProperty("f", (uint)zone["f"])); //  флаги геозон 0x20 – отображать фигуру,   0x40 – не минифицировать геозону.
            JArray zonesids = new JArray(); //new JArray() { (uint)zone["id"] })
            List<uint> ggroup = _GEOZONEGROUPS.Where(gzgroup => gzgroup.Name == "Зоны выгрузки").Select(g => g.Zones).ToList()[0];
            ggroup.Add((uint)zone["id"]);
            foreach (uint zoneitem in ggroup)
            {
               zonesids.Add(zoneitem);
            }
            data.Add(new JProperty("zns", zonesids));
            //rgba(170, 0, 162, 1); AA00A2
            //-------------------------Задание координат и радиуса зоны--------------------
            //JArray points = new JArray() {}; // массив координат геозон и радиус
            //data.Add(new JProperty("p", points));
            //-----------------------------------------------------------------------------
            string answer = POST(null, cmd, data.ToString(Newtonsoft.Json.Formatting.None));
            res = JArray.Parse(answer);
            FillGeozones();
         }
         catch(Exception ex)
         { 

         }
         
         return res;
      }


      public JObject GetAlZonesGroupsFromWialon()
      {

         /*
         svc=core/search_items&params={"spec":{
                         "itemsType":<text>,	
                         "propName":<text>,	
                         "propValueMask":<text>,	
                         "sortType":<text>,
                         "propType":<text>,
                         "or_logic":<bool>	
                          },
                          "force":<uint>,			
                          "flags":<long>,			
                       "from":<uint>,			
                          "to":<uint>} 
          */
         JObject resault = new JObject();
         JArray zones_array = new JArray();
         string command = Commands.Search;//"core/search_items";
         JObject data = new JObject();
         JObject spec = new JObject();
         spec.Add("itemsType", ItemsType.AVL_RESOURCE);//"avl_resource"
         spec.Add("propName", "");
         spec.Add("propValueMask", "");
         spec.Add("sortType", "");
         spec.Add("propType", PropType.None);
         spec.Add("or_logic", true);

         data.Add("spec", spec);
         data.Add("force", 1);
         long fl = FLAGS.GeoZoneGroups| FLAGS.BaseFlag | FLAGS.GUID;
         data.Add("flags", fl);
         data.Add("from", 0);
         data.Add("to", 0);
         string ssres = GET(null, command, data.ToString(Newtonsoft.Json.Formatting.None));
         // Console.WriteLine(ssres);
         Newtonsoft.Json.Linq.JObject jobj = JsonConvert.DeserializeObject<Newtonsoft.Json.Linq.JObject>(ssres);
         if (jobj.ContainsKey("items"))
         {

            JArray tmp = (JArray)jobj["items"];
            foreach (JObject item in tmp)
            {

               if (item.ContainsKey("zg"))
               {
                  var id = item["id"];
                  resault.Add("id", id);
                  JObject zones = (JObject)(((JObject)item)["zg"]);
                  foreach (var zone in zones)
                  {
                     // Console.WriteLine(zone.Value);
                     zones_array.Add(zone.Value);
                     //Console.WriteLine( "Зона "+zone.Value["id"]+": "+ zone.Value["n"]);              
                  }
                  resault.Add("zones", zones_array);
               }

            }
         }
         return resault;
      }

      public JObject CreateGeozone(JObject zone)
      {
         /*
          * 
          "[91,{\"n\":\"Тестовая геозона\",\"d\":\"Учимся создавать геозоны\",\"id\":91,\"f\":112,\"t\":3,\"w\":100,\"e\":51597,\"c\":11141282,\"i\":4294967295,\"libId\":0,\"path\":\"\",\"b\":{\"min_x\":61.3214826256,\"min_y\":56.8163322867,\"max_x\":61.3247576088,\"max_y\":56.8181281625,\"cen_x\":61.3231201172,\"cen_y\":56.8172302246},\"ct\":1634128677,\"mt\":1634128677}]\n"* 
         svc=resource/update_zone&params={"itemId":<long>,
				          "id":<long>,
				          "callMode":<text>,
				          "n":<text>,
				          "d":<text>,
				          "t":<int>,
				          "w":<int>,
				          "f":<uint>,
				          "c":<int>,
				          "tc":<uint>,
				          "ts":<uint>,
				          "min":<uint>,
				          "max":<uint>,
				          "path":<text>,
				          "libId":<long>,
				          "oldItemId":<uint>,
				          "oldZoneId":<uint>,
				          "jp":<JSON>,
				          "p":[				
					         {
						         "x":<double>,
						         "y":<double>,
						         "r":<int>
					         }
				          ]} 
         "{\"error\":4, \"reason\":\"VALIDATE_PARAMS_ERROR: {n: text, t: uint, w: uint, f: uint, c: uint, p: [{x: double, y: double, r: uint}]}\"}\n"
          */

         JObject res = new JObject();
         
         JObject data = new JObject();
         try
         {
            string cmd = Commands.CreateGeozone;
            data.Add(new JProperty("itemId", (long)_RootId));
            data.Add(new JProperty("id", (long)0)); //id геозоны. При создании = 0
            data.Add(new JProperty("callMode", "create")); // режим вызова - создать
            data.Add(new JProperty("t", 3)); // тип: 1 - линия, 2 - полигон, 3 - круг
            data.Add(new JProperty("n", (string)zone["name"])); // имя гозоны
            data.Add(new JProperty("d", (string)zone["desc"])); // описание геозоны
            data.Add(new JProperty("w", (uint)100));
            data.Add(new JProperty("c", (uint)0xAA00A2));
            data.Add(new JProperty("f", 0x20 | 0x40)); //  флаги геозон 0x20 – отображать фигуру,   0x40 – не минифицировать геозону.
                                                       //rgba(170, 0, 162, 1); AA00A2
                                                       //-------------------------Задание координат и радиуса зоны--------------------
            JArray points = new JArray() { new JObject() { new JProperty("x", (double)zone["x"]), new JProperty("y", (double)zone["y"]), new JProperty("r", 100) } }; // массив координат геозон и радиус
            data.Add(new JProperty("p", points));
            //-----------------------------------------------------------------------------
            string answer = POST(null, cmd, data.ToString(Newtonsoft.Json.Formatting.None));
            JArray janswer = JArray.Parse(answer);
            res = (JObject)janswer[1];
            

            FillGeozones();
         }
         catch(Exception ex)
         {

         }

         return res;
      }
      public JObject GetCoordsByAddr(JObject addr)
      {

         /*
          * https://search-maps.wialon.com/{host}/gis_search?country=<text>&region=<text>&city=<text>&street=<text>&flags=<uint>&count=<uint>&indexFrom=<uint>&uid=<long>
         */

         /*
            Флаг	Описание
            0x0	искать страну
            0x1	искать регион (область)
            0x2	искать город
            0x3	искать улицу
            0x4	искать дом
            0x100	Добавить поля с форматированной строкой адреса в результат
            0x200	Добавить название карты в результат
            0x400	Добавить координаты в результат
          */


         JObject res = new JObject();
         if (addr.Count == 0) { return res; }
         string url = "http://search-maps.wialon.com/hst-api.wialon.com/";
         string count ="&count="+1;
         string indexfrom = "&indexFrom=" + 0;
         string uid = "&uid=" + UserId;
         int flags = 0x400 | 0x200 | 0x100 | 0x4;
         string data = "";
         string City = "";
         string Country = "";
         string Street = "";
         string Region = "";
         string sflags = "&flags=" + flags;
         if (addr.ContainsKey("City"))
         {
            if (!String.IsNullOrEmpty((string)addr["City"]))
            {
               City = "&city=" + (string)addr["City"];
            }
         }
         if (addr.ContainsKey("Country"))
         {
            if (!String.IsNullOrEmpty((string)addr["Country"]))
            {
               Country = "&country=" + (string)addr["Country"];
            }
         }
         if (addr.ContainsKey("Street"))
         {
            if (!String.IsNullOrEmpty((string)addr["Street"]))
            {
               Street = "&street=" + (string)addr["Street"];
            }
         }
         if (addr.ContainsKey("Region"))
         {
            if (!String.IsNullOrEmpty((string)addr["Region"]))
            {
               Region = "&region=" + (string)addr["Region"];
            }
         }
         data = String.Format("{0}{1}{2}{3}{4}{5}{6}{7}", Country, Region, City, Street, sflags, count, indexfrom, uid).Replace(" ", "%20");         

         string cmd = Commands.GetCoordsForAddr;

         string endurl = String.Format("{0}{1}?{2}", url, cmd, data);

         var result = _HTTP_CLIENT.GetAsync(endurl);//_HTTP_CLIENT.PostAsync(address, content).Result;
         result.Wait();
         var r = result.Result;
         if (r.StatusCode == System.Net.HttpStatusCode.OK)
         {
            try
            {
               var cont = r.Content.ReadAsStringAsync();
               cont.Wait();
               var scont = cont.Result.ToString();
               Newtonsoft.Json.Linq.JArray contentdata = JsonConvert.DeserializeObject<Newtonsoft.Json.Linq.JArray>(scont);
               var items = (JObject)contentdata[0];
               if (items.ContainsKey("items"))
               {
                  var item = (JObject)((JArray)items["items"])[0];
                  var x = (double)item.GetValue("x");
                  var y = (double)item.GetValue("y");
                  var fp = (string)item.GetValue("formatted_path");
                  JProperty xprop = new JProperty("x", x);
                  JProperty yprop = new JProperty("y", y);
                  JProperty fpath = new JProperty("formatted_path", fp);

                  res.Add(xprop);
                  res.Add(yprop);
                  res.Add(fpath);
               }
            }
            catch (Exception ex)
            {
            }
         }
         return res;
      }

      public JObject GetCoordsByPhrase(string phrase)
      {
         
         /*https://search-maps.wialon.com/hst-api.wialon.com/gis_searchintelli?
            phrase = 6а % 20скрыганова % 20минск
                  & count = 2
                  & indexFrom = 0
                  & uid = 50935*/
         JObject res = new JObject();
         if (String.IsNullOrEmpty(phrase)) { return res; }


         
         string url = "http://search-maps.wialon.com/hst-api.wialon.com/";
         string cmd = Commands.GetCoordsForAddrByPhrase;
         //phrase = "6а%20скрыганова%20минск";
         string data = "phrase=" +phrase.Replace(" ","%20").Replace(",", "%2") +"&count=1"+"&uid="+UserId;
         string endurl = String.Format("{0}{1}?{2}", url, cmd, data);
         var result = _HTTP_CLIENT.GetAsync(endurl);//_HTTP_CLIENT.PostAsync(address, content).Result;
         result.Wait();
         var r = result.Result;
         if(r.StatusCode == System.Net.HttpStatusCode.OK)
         {
            try {
               var cont = r.Content.ReadAsStringAsync();
               cont.Wait();
               var scont = cont.Result.ToString();
               Newtonsoft.Json.Linq.JArray contentdata = JsonConvert.DeserializeObject<Newtonsoft.Json.Linq.JArray>(scont);
               var items = (JObject)contentdata[0];
               if (items.ContainsKey("items"))
               {
                  var item = (JObject)((JArray)items["items"])[0];
                  var x = (double)item.GetValue("x");
                  var y = (double)item.GetValue("y");
                  var fp = (string)item.GetValue("formatted_path");
                  JProperty xprop = new JProperty("x", x);
                  JProperty yprop = new JProperty("y", y);
                  JProperty fpath = new JProperty("formatted_path", fp);

                  res = item;
               }
            }
            catch (Exception ex)
            {               
            }           
         }
         return res;
      }

      public void Start()
      {
         if (_WORK_THREAD == null)
         {
            _WORK_THREAD = new Thread(WorkThread);
            _WORK_THREAD.Start();
            return;
         }
         if (_WORK_THREAD.IsAlive) { return; }
         else
         {
            _WORK_THREAD = new Thread(WorkThread);
            _WORK_THREAD.Start();
         }
         
      }

      private void EventQueueHandler()
      {

      }



      private void WorkThread()
      {
         TimerCallback callback = new TimerCallback(GetNewEventsByWialon);
         Timer checker = new Timer(callback, null, TimeSpan.FromSeconds(1), TimeSpan.FromSeconds(10));

         while (_CONNECTED)
         {
            if (_NEW_EVENTS_PRESENT.WaitOne(0))
            {
               if (_EVENTS_QUEUE.Count > 0)
               {
                  JObject jevent = _EVENTS_QUEUE.First();
                  string carname = GetCarNameById((int)jevent["i"]);
                  Console.WriteLine(String.Format("=================================================================================================================================\r\n\tНовое событие по машине {0}\r\n{1}", carname, jevent.ToString(Newtonsoft.Json.Formatting.None)));
                  // Console.WriteLine(jevent.ToString(Newtonsoft.Json.Formatting.None));
                  _EVENTS_QUEUE.RemoveAt(0);
                  //int count = events.Count;
                  //foreach (JObject item  in events)
                  //{
                  //  Console.WriteLine(item.ToString(Newtonsoft.Json.Formatting.None));
                  //}
               }

               if (_EVENTS_QUEUE.Count() == 0)
               {
                  _NEW_EVENTS_PRESENT.Reset();

               }
            }
         }

         checker.Dispose();
         checker = null;
      }

   }
}
