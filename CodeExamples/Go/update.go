// update
package main

import (
	//	"bytes"
	"bytes"
	"encoding/json"
	"encoding/xml"

	"errors"
	//"flag"
	"fmt"
	//"io/ioutil"
	"log"
	"os"

	//"strconv"
	"strings"
	"time"

	"github.com/tarm/serial"
	"github.com/xlab/at/sms"
	"github.com/xlab/at/util"
)

var (
	TASKS   Task
	OBJECTS Query

	OPSOSS Operators
	SPORT  *serial.Port
	//RESETM1          string = "OTAP_IMPNG\nPWD:\nAPNORNUM:%s\nNETUSER:%s\nNETPWD:%s\nJADURL:SOMEURL\nAPPDIR:a:/dist\n"
	RESETM1 string = "OTAP_IMPNG\nPWD:\nAPNORNUM:%s\nNETUSER:%s\nNETPWD:%s\nJADURL:SOMEURL\nAPPDIR:a:/\n"
	//RESETM1          string = "OTAP_IMPNG\nPWD:\nAPNORNUM:%s\nNETUSER:%s\nNETPWD:%s\nJADURL:SOMEURL\nAPPDIR:a:/\n"
	RESETM2          string = "OTAP_IMPNG\nPWD:\nBEARER:gprs\nNOTIFYURL:SOMEURL\nSTART:install\n"
	COMMAND_TEMPLATE string = "%s\r"
)

//---------------------------------------------------------------
type Query struct {
	// Have to specify where to find episodes since this
	// doesn't match the xml tags of the data that needs to go into it
	XMLName    xml.Name `xml:"datatab"`
	Recordlist []Record `xml:"record"`
}

type Record struct {
	XMLName xml.Name `xml:"record"`
	F0      int      `xml:"f0"`
	F1      string   `xml:"f1"`
	F2      string   `xml:"f2"`
	F3      string   `xml:"f3"`
	F4      string   `xml:"f4"`
	F5      string   `xml:"f5"`
	F6      string   `xml:"f6"`
	F7      string   `xml:"f7"`
	F8      string   `xml:"f8"`
	F9      string   `xml:"f9"`
	F10     string   `xml:"f10"`
	F11     string   `xml:"f11"`
	F12     string   `xml:"f12"`
	F13     string   `xml:"f13"`
	F14     string   `xml:"f14"`
	F15     string   `xml:"f15"`
	F16     string   `xml:"f16"`
	F17     string   `xml:"f17"`
	F18     string   `xml:"f18"`
}

//---------------------------------------------------------------

//---------------------------------------------------------------
type Operators struct {
	Operators []Operator `json:"operators"`
}

type Operator struct {
	Name string `json:"name"`
	APN  string `json:"apn"`
	USER string `json:"user"`
	PASS string `json:"password"`
}

//---------------------------------------------------------------
type Task struct {
	TaskItems []string `json:"items"`
}
type TaskItem struct {
	Item string
}

//---------------------------------------------------------------

func init() {
	loadObjects()
	loadOPSOSS()
	loadTask()
	fmt.Println(TASKS)

}

func main() {
	fmt.Println("Hello World!")
	openSerialPort("com17")
	scanTask()
}

func loadOPSOSS() {
	//открываем файл с пользователями
	jsonFile, err := os.Open("provaders.json")
	if err != nil {
		fmt.Println("Error opening file:", err)
		return
	}

	// get the file size
	stat, err := jsonFile.Stat()
	if err != nil {
		return
	}
	// read the file
	bs := make([]byte, stat.Size())
	_, err = jsonFile.Read(bs)
	if err != nil {
		fmt.Println("Error opening file:", err)
		return
	}
	defer jsonFile.Close()
	err = json.Unmarshal(bs, &OPSOSS)
	if err != nil {
		fmt.Println("Error parcing file:", err)
		return
	}
}

func loadObjects() {

	//открываем файл с объектами
	xmlFile, err := os.Open("objects.xml")
	if err != nil {
		fmt.Println("Error opening file:", err)
		return
	}

	// get the file size
	stat, err := xmlFile.Stat()
	if err != nil {
		return
	}
	// read the file
	bs := make([]byte, stat.Size())
	_, err = xmlFile.Read(bs)
	if err != nil {
		fmt.Println("Error opening file:", err)
		return
	}
	defer xmlFile.Close()
	err = xml.Unmarshal(bs, &OBJECTS)
	if err != nil {
		fmt.Println("Error parcing file:", err)
		return
	}

}

func loadTask() {
	//открываем файл с заданием
	jsonFile, err := os.Open("task.json")
	if err != nil {
		fmt.Println("Error opening file:", err)
		return
	}

	// get the file size
	stat, err := jsonFile.Stat()
	if err != nil {
		return
	}
	// read the file
	bs := make([]byte, stat.Size())
	_, err = jsonFile.Read(bs)
	if err != nil {
		fmt.Println("Error opening file:", err)
		return
	}
	defer jsonFile.Close()
	err = json.Unmarshal(bs, &TASKS)
	if err != nil {
		fmt.Println("Error parcing file:", err)
		return
	}
}

func getRecord(id string) (Record, error) {
	var r Record
	var err error
	for i := 0; i < len(OBJECTS.Recordlist); i++ {
		if id == OBJECTS.Recordlist[i].F12 {
			r = OBJECTS.Recordlist[i]
			err = nil
			break
		} else {
			err = errors.New("Объект не найден")
			continue
		}
	}
	return r, err
}

func getOperator(name string) (Operator, error) {
	res := Operator{}
	var err error
	for _, op := range OPSOSS.Operators {
		if op.Name == name {
			res = op
			break
		}
	}
	return res, err
}

func updateObject(id string, port *serial.Port) error {
	fmt.Println(fmt.Sprintf("try update %s object ", id))
	var err error
	answer := ""
	r, err := getRecord(id)
	if err == nil {
		if r.F6 != "-" {
			opname := strings.ToUpper(r.F17)
			fmt.Println(fmt.Sprintf("Operator %s ", opname))
			fmt.Println(fmt.Sprintf("Sim number %s ", r.F6))
			switch opname {
			case "БИЛАЙН":
				opname = "BEELINE"
			case "МЕГАФОН":
				opname = "MEGAFON"
			case "МОТИВ":
				opname = "MOTIV"
			case "МТС":
				opname = "MTS"
			case "УРАЛВЕСТКОМ":
				opname = "TELE2"
			case "BEELINE":
				opname = "BEELINE"
			case "MOTIV":
				opname = "MOTIV"
			case "MTS":
				opname = "MTS"
			case "TELE2":
				opname = "TELE2"
			case "MEGAFON":
				opname = "MEGAFON"
			}
			op, _ := getOperator(opname)
			txt := fmt.Sprintf(RESETM1, op.APN, op.USER, op.PASS)
			mess := sms.Message{
				Text:                txt,
				Encoding:            sms.Encodings.Gsm8Bit_2,
				Type:                sms.MessageTypes.Submit,
				Address:             sms.PhoneNumber(r.F6),
				ProtocolIdentifier:  0x7d,
				StatusReportRequest: true,
			}
			//fmt.Println(mess)
			var b bytes.Buffer
			n, data, _ := mess.PDU()
			str := util.HexString(data)
			b.Write([]byte(str))
			b.WriteByte(byte(0x1A))
			log.Printf("lenght: %d", n)
			log.Println(str)
			//						answer, err = sendCommand(SPORT, "AT+CMGS=?")
			//						log.Printf(strings.Replace(answer, "\r\n", "|", -1))
			cmd := fmt.Sprintf("AT+CMGS=%d", n)
			log.Println(cmd)
			answer, err = sendCommand(port, cmd, 5)
			log.Printf(strings.Replace(answer, "\r\n", "|", -1))
			if strings.Contains(answer, ">") {
				port.Write(b.Bytes())
				time.Sleep(time.Second * 20)
				ans, err := readAnswer(port)
				if err != nil {
					log.Fatal(err)
				} else {
					log.Printf("%s", ans)
					if !strings.Contains(ans, "ERROR") {
						mess := sms.Message{
							Text:                RESETM2,
							Encoding:            sms.Encodings.Gsm8Bit_2,
							Type:                sms.MessageTypes.Submit,
							Address:             sms.PhoneNumber(r.F6),
							ProtocolIdentifier:  0x7d,
							StatusReportRequest: true,
						}

						var b bytes.Buffer
						n, data, _ := mess.PDU()
						str := util.HexString(data)
						b.Write([]byte(str))
						b.WriteByte(byte(0x1A))
						log.Printf("lenght: %d", n)
						log.Println(str)
						//						answer, err = sendCommand(SPORT, "AT+CMGS=?")
						//						log.Printf(strings.Replace(answer, "\r\n", "|", -1))
						cmd := fmt.Sprintf("AT+CMGS=%d", n)
						log.Println(cmd)
						answer, err = sendCommand(port, cmd, 1)
						log.Printf(strings.Replace(answer, "\r\n", "|", -1))
						if strings.Contains(answer, ">") {
							port.Write(b.Bytes())
							time.Sleep(time.Second * 15)
							ans, err := readAnswer(port)
							if err != nil {
								log.Fatal(err)
							}
							if strings.Contains(ans, "ERROR") {
								return errors.New(fmt.Sprintf("Не получилось отправить сообщение: %s", ans))

							}
						}
					}
				}

			} else {
				return errors.New(fmt.Sprintf("Не получилось отправить сообщение: %s", answer))
			}

		}
	}
	return err
}

func scanTask() {
	//	var err error
	//	answer := ""
	//	r, err := getRecord(id)
	c := new(serial.Config)
	c.Name = "com17"
	c.Baud = 115200
	c.ReadTimeout = time.Second * 30
	c.Size = serial.DefaultSize
	c.Parity = serial.ParityNone
	c.StopBits = serial.Stop1

	SPORT, err := serial.OpenPort(c)
	if err != nil {
		log.Fatal(err)
	}
	defer SPORT.Close()
	answergsm, err := sendCommand(SPORT, "AT", 1)
	if err != nil {
		log.Fatal(err)
	}
	log.Printf(strings.Replace(answergsm, "\r\n", "|", -1))

	answergsm, err = sendCommand(SPORT, "AT+IPR=115200", 1)
	if err != nil {
		log.Fatal(err)
	}
	log.Printf(strings.Replace(answergsm, "\r\n", "|", -1))

	answergsm, err = sendCommand(SPORT, "ATE0", 1)
	if err != nil {
		log.Fatal(err)
	}
	log.Printf(strings.Replace(answergsm, "\r\n", "|", -1))

	answergsm, err = sendCommand(SPORT, "AT&D0", 1)
	if err != nil {
		log.Fatal(err)
	}
	log.Printf(strings.Replace(answergsm, "\r\n", "|", -1))

	answergsm, err = sendCommand(SPORT, "AT+CMGF=0", 1)
	if err != nil {
		log.Fatal(err)
	}
	log.Printf(strings.Replace(answergsm, "\r\n", "|", -1))

	answergsm, err = sendCommand(SPORT, "ATI", 1)
	if err != nil {
		log.Fatal(err)
	}
	log.Printf(strings.Replace(answergsm, "\r\n", "|", -1))

	for i := 0; i < len(TASKS.TaskItems); i++ {
		updateObject(TASKS.TaskItems[i], SPORT)
	}
}

func sendCommand(port *serial.Port, command string, timeout time.Duration) (string, error) {
	n := 0

	var err error
	cmds := fmt.Sprintf(COMMAND_TEMPLATE, command)

	log.Printf("Send command: " + cmds)
	if port == nil {
		log.Fatal(errors.New("Нулевой указатель на com порт"))
	}
	n, err = port.Write([]byte(cmds))

	if n == 0 || n < 0 {
		log.Println("Sended %x bytes", n)
	}
	if err != nil {
		log.Fatal(err)
	}
	//	port.Flush()
	time.Sleep(time.Second * timeout)
	return readAnswer(port)
}

func readAnswer(port *serial.Port) (string, error) {
	answer := ""
	buf := make([]byte, 200)
	var err error
	n, err := port.Read(buf)
	//	port.Flush()
	if n == 0 || n < 0 {
		log.Printf("Readed %x bytes", n)
	}
	if err != nil {
		log.Fatal(err)
	}
	answer = string(buf[:n])

	if answer != "" {
		for strings.HasSuffix(answer, "\r\n") {
			answer = strings.TrimSuffix(answer, "\r\n")
		}
		for strings.HasPrefix(answer, "\r\n") {
			answer = strings.TrimPrefix(answer, "\r\n")
		}

	}
	return answer, err
}

func openSerialPort(port string) {
	c := new(serial.Config)
	c.Name = port
	c.Baud = 115200
	c.ReadTimeout = time.Second * 30
	c.Size = serial.DefaultSize
	c.Parity = serial.ParityNone
	c.StopBits = serial.Stop1

	SPORT, err := serial.OpenPort(c)
	if err != nil {
		log.Fatal(err)
	}
	defer SPORT.Close()
	answergsm, err := sendCommand(SPORT, "AT", 1)
	if err != nil {
		log.Fatal(err)
	}
	log.Printf(strings.Replace(answergsm, "\r\n", "|", -1))

	answergsm, err = sendCommand(SPORT, "AT+IPR=115200", 1)
	if err != nil {
		log.Fatal(err)
	}
	log.Printf(strings.Replace(answergsm, "\r\n", "|", -1))

	answergsm, err = sendCommand(SPORT, "ATE0", 1)
	if err != nil {
		log.Fatal(err)
	}
	log.Printf(strings.Replace(answergsm, "\r\n", "|", -1))

	answergsm, err = sendCommand(SPORT, "AT&D0", 1)
	if err != nil {
		log.Fatal(err)
	}
	log.Printf(strings.Replace(answergsm, "\r\n", "|", -1))

	answergsm, err = sendCommand(SPORT, "AT+CMGF=0", 1)
	if err != nil {
		log.Fatal(err)
	}
	log.Printf(strings.Replace(answergsm, "\r\n", "|", -1))

	answergsm, err = sendCommand(SPORT, "ATI", 1)
	if err != nil {
		log.Fatal(err)
	}
	log.Printf(strings.Replace(answergsm, "\r\n", "|", -1))
}

func openPort(name string) (*serial.Port, error) {
	log.Printf("Open port")
	var err error
	c := &serial.Config{Name: name, Baud: 115200, ReadTimeout: time.Second * 15} /*ReadTimeout: time.Second * 15*/
	s, err := serial.OpenPort(c)
	//	defer s.Close()
	if err != nil {
		log.Fatal(err)
	}
	return s, err
}
