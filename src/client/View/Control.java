package View;

import java.io.*;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Vector;
import java.util.regex.Pattern;

import javafx.concurrent.Task;
import javafx.scene.control.*;
import javafx.scene.shape.Line;

import network.ClientNetManager;
import database.AccountInfo;
import database.GroupList;
import database.FriendList;
import chatbean.*;
import message.*;

public class Control
{
	public static Welcome welcome;
	private Register register;
	private Forget forget;
	public static MainWindow mainwindow;
	public static HomePage homepage;
	public static SearchNew searchnew;
	public static  Chat chat;
	public static AddGroup addgroup;
	public static FriendPage friendpage;
	public static ApplyAdd applyAdd;
	public static Progress progress;

	public static AccountInfo usrInfo;
	public  static FriendList frdList;
	public static ChattingRecord chattingRecord;
	public static ClientNetManager network=null;
	public static Vector<ChatBean> ApplyFriend;
	private String usr_pw;
	private String IP;
	private String port;



	/********************  Global init and execution logic *******************/

	public Control() throws IOException
	{
		welcome = new Welcome();
		register = new Register();
		forget = new Forget();
		mainwindow = new MainWindow();
		searchnew = new SearchNew();
		searchnew.setModality(mainwindow);
		chat = new Chat();
		homepage = new HomePage();
		usrInfo = new AccountInfo();
		frdList = new FriendList();
		chattingRecord = new ChattingRecord();
		friendpage = new FriendPage();
		addgroup = new AddGroup();
		progress = new Progress();
		addgroup.setModality(mainwindow);
		homepage.setModality(mainwindow);
		friendpage.setModality(mainwindow);
		basic_event_init();
		welcome.show();

		ApplyFriend = new Vector<>();
		//load state information
		init_state();

		//used for test
		/*
		usrInfo.id = 2;
		usrInfo.NickName = "senpai";
		usrInfo.Sex = "1";
		usrInfo.Mail = "1145@14.com";
		usrInfo.Birthday = "0000-0-0";
		frdList = new FriendList();
		frdList.groupNum = 2;
		frdList.Groups = new GroupList[2];
		for(int i=0;i<2;++i)
		{
			frdList.Groups[i] = new GroupList();
			GroupList group = frdList.Groups[i];
			if(i==0)
			{
				group.GroupName = new String("系统助手");
				group.FriendNum = 1;
				group.FriendAccount = new AccountInfo[1];
				group.FriendRemarks = new String[1]; group.FriendRemarks[0] ="helper";
			}
			else
			{
				group.GroupName = new String("我的好友");
				group.FriendNum = 1;
				group.FriendAccount = new AccountInfo[1]; group.FriendAccount[0]=new AccountInfo();
				group.FriendRemarks = new String[1]; group.FriendRemarks[0] ="my friend";
			}
		}*/
	}

	//execution buttons of the whole program
	public void exec() throws IOException
	{
		Enter();
		Save();
		getCode();
		Change();
		Update();
		sendMsg();
		AddNewGroup();
		addNew();
	}

	//set action for some buttons
	public void basic_event_init()
	{
		/*  Welcome Page  */
		Button regi = ((Button) welcome.search("Register"));
		Button forg = ((Button) welcome.search("Forget"));
		regi.setOnAction(event ->{
			welcome.hide();
			welcome.clear();
			register.show();
		});
		forg.setOnAction(event ->{
			welcome.hide();
			welcome.clear();
			forget.show();
		});
		/*   Register Page  */
		Button cancel_r = ((Button) register.search("Cancel"));
		cancel_r.setOnAction(event ->{
			register.hide();
			register.clear();
			try
			{
				init_state();
			}catch (IOException e)
			{
				e.printStackTrace();
			}
			welcome.show();
		});
		/*   Forget Page   */
		Button cancel_f = ((Button) forget.search("Cancel"));
		cancel_f.setOnAction(event ->{
			forget.hide();
			forget.clear();
			forget.get = 0;
			((Button)forget.search("Get")).setText("获取");
			try
			{
				init_state();
			}catch (IOException e)
			{
				e.printStackTrace();
			}
			welcome.show();
		});
		/*   MainWindow Page   */
		Button msg = ((Button) mainwindow.search("Message"));
		Button frd = ((Button) mainwindow .search("Friend"));
		Button add = ((Button) mainwindow.search("Add"));
		Button head = ((Button) mainwindow.search("Head"));
		msg.setOnAction(event -> {
			((Line) mainwindow.search("MsgLine")).setVisible(true);
			((Line) mainwindow.search("FrdLine")).setVisible(false);
			((ListView) mainwindow.search("message")).setManaged(true);
			((ListView) mainwindow.search("message")).setVisible(true);
			//((ListView) mainwindow.search("message")).getItems().clear();
			//mainwindow.addFriend(frdList);
		});
		frd.setOnAction(event ->{
			((Line) mainwindow.search("FrdLine")).setVisible(true);
			((Line) mainwindow.search("MsgLine")).setVisible(false);
			((ListView) mainwindow.search("message")).setVisible(false);
			((ListView) mainwindow.search("message")).setManaged(false);
			//((ListView) mainwindow.search("FriendList")).getItems().clear();
			//mainwindow.addGroup(frdList);
		});
		add.setOnAction(event ->{
			searchnew.show();
		});
		head.setOnAction(event ->{
			((Label) homepage.search("nickname")).setText(usrInfo.getNickName()+"\n"+"("+usrInfo.id+")");
			((TextField) homepage.search("account")).setText(usrInfo.getNickName());
			((TextField) homepage.search("label")).setText(((Label) mainwindow.search("Signature")).getText());
			((TextField) homepage.search("birth")).setText(usrInfo.getBirthday());
			((TextField) homepage.search("email")).setText(usrInfo.Mail);
			//System.out.println(usrInfo.getSex());
			//System.out.println(usrInfo.getSex().equals("1"));
			if(usrInfo.getSex().equals("1"))
			{
				((Label) homepage.search("sexb")).setVisible(true);
				((Label) homepage.search("sexb")).setManaged(true);
				((Label) homepage.search("sexg")).setVisible(false);
				((Label) homepage.search("sexg")).setManaged(false);
			}
			else if(usrInfo.getSex().equals("2"))
			{
				((Label) homepage.search("sexg")).setVisible(true);
				((Label) homepage.search("sexg")).setManaged(true);
				((Label) homepage.search("sexb")).setVisible(false);
				((Label) homepage.search("sexb")).setManaged(false);
			}
			else
			{
				((Label) homepage.search("sexg")).setVisible(false);
				((Label) homepage.search("sexg")).setManaged(false);
				((Label) homepage.search("sexb")).setVisible(false);
				((Label) homepage.search("sexb")).setManaged(false);
			}
			homepage.setNoAction();
			homepage.show();
		});
	}

	// remember, auto login, or default
	public void init_state() throws IOException
	{
		File f = new File("state.conf");
		if(!f.exists())
			f.createNewFile();
		BufferedReader br = new BufferedReader(new FileReader(f));
		String[] data = {"", "", "", "", ""};
		String line = null;
		int cnt = 0;
		while((line=br.readLine()) != null && cnt<5)
		{
			data[cnt++] = line;
		}
		br.close();
		//for(int i=0;i<3;++i)
			//System.out.println(data[i]);
		if(data[4]!=null&&(!data[4].equals("")))
		{
			network = new ClientNetManager(data[3], new Integer(data[4]).intValue());
		}
		if(network == null)
		{
			welcome.alert1.exec(welcome, "请在配置文件中输入正确的IP地址和端口号");
		}
		else
		{
			IP = data[3]; port = data[4];
			Button sel = ((Button) welcome.search("AutoLogin1"));
			Button unsel = ((Button) welcome.search("AutoLogin"));
			Button rem = ((Button) welcome.search("Remember1"));
			Button unrem = ((Button) welcome.search("Remember"));
			TextField user = ((TextField) welcome.search("UserName"));
			PasswordField pw = ((PasswordField) welcome.search("Password"));
			if(data[1].equals("2"))
			{
				user.setText(data[0]);
				pw.setText(data[2]);
				//System.out.println(user.getText());
				//System.out.println((pw.getText()));
				unsel.setManaged(false);
				unsel.setVisible(false);
				sel.setManaged(true);
				sel.setVisible(true);
				rem.setManaged(true);
				rem.setVisible(true);
				unrem.setManaged(false);
				unrem.setVisible(false);
				welcome.ClrErrTip();
				welcome.loading.exec(welcome, "正在连接...");
				Task task = new Task<ChatBean>(){
					public ChatBean call() throws InterruptedException {
						return SEND_ID_AND_PASSWORD_TO_SERVER(Integer.parseInt(data[0]), data[2]);
					}
				};
				task.setOnSucceeded(taskFinishEnent ->{
					ChatBean state = (ChatBean)task.getValue();
					if(state == null)//fail to connect
					{
						welcome.loading.close();
						welcome.alert1.exec(welcome, "连接失败，请检查网络");
					}
					else if(state.type == TypeValue.REPLY_BAD_ID)//Account don't exist
					{
						welcome.loading.close();
						welcome.ClrErrTip();
						welcome.setErrTip("ACCErr", "账号不存在！");
					}
					else if(state.type == TypeValue.REPLY_CHECK_FAILED)
					{
						welcome.loading.close();
						welcome.ClrErrTip();
						welcome.setErrTip("PWErr", "密码错误！");
					}
					else if(state.type == TypeValue.REPLY_SERVER_ERROR)
					{
						welcome.loading.close();
						welcome.ClrErrTip();
						welcome.alert1.exec(welcome, "服务器发生异常，请稍后再试");
					}
					else if(state.type == TypeValue.REPLY_OK)
					{
						welcome.ClrErrTip();
						usrInfo = state.accountInfo;
						frdList = state.friendList;
						chattingRecord = state.chattingRecord;
						Logging();
					}
					else
					{
						welcome.loading.close();
						welcome.ClrErrTip();
						welcome.alert1.exec(welcome, "登录失败，请重试");
					}
				} );
				task.setOnCancelled(event1 -> {
					welcome.loading.close();
					welcome.alert1.exec(welcome, "发生错误，请重试");
				});
				task.setOnFailed(event1 -> {
					welcome.loading.close();
					welcome.alert1.exec(welcome, "发生错误，请重试");
				});
				new Thread(task).start();
			}
			else if(data[1].equals("1"))
			{
				user.setText(data[0]);
				pw.setText(data[2]);
				//System.out.println(user.getText());
				//System.out.println((pw.getText()));
				rem.setManaged(true);
				rem.setVisible(true);
				unrem.setManaged(false);
				unrem.setVisible(false);
			}
			else
			{
				//System.out.println("0");
				user.setText(data[0]);
				pw.setText("");
			}
		}
	}

	/********************     Used in Welcome window    *******************/

	//Logging...
	public void Logging()
	{
		String User = ((TextField) welcome.search("UserName")).getText();
		String Password = ((PasswordField) welcome.search("Password")).getText();
		usr_pw = Password;
		//save the logging state (remember password or auto login)
		File f = new File("state.conf");
		try
		{
			if (!f.exists())
				f.createNewFile();
			FileWriter fw = new FileWriter(f);
			fw.write("");
			String st = "";
			String pwsv="";
			Button sel = ((Button) welcome.search("AutoLogin1"));
			Button rem = ((Button) welcome.search("Remember1"));
			if(sel.isManaged())
			{
				st = "2";
				pwsv = Password;
			}
			else if(rem.isManaged())
			{
				st = "1";
				pwsv = Password;
			}
			else
			{
				st = "0";
			}
			String out = User+"\n"+st+"\n"+pwsv+"\n"+IP+"\n"+port+"\n";
			fw.write(out);
			fw.flush();
			fw.close();
		}catch(IOException e)
		{
			welcome.alert1.exec(welcome, "数据本地化失败！");
		}

		//((Label) welcome.loading.search("Info")).setText("正在加载数据，请稍候...");
		if(usrInfo == null)// fail to connect
		{
			welcome.loading.close();
			welcome.alert1.exec(welcome, "数据初始化失败，请检查网络");
		}
		else
		{
			String label = "Happy, Lucky, Smile, Yeah!";
			mainwindow.setPersonInfo(usrInfo.getNickName(), label);
			if(frdList == null)
			{
				welcome.loading.close();
				welcome.alert1.exec(welcome, "数据初始化失败，请检查网络");
			}
			else
			{
				if(mainwindow.addFriend(frdList, chattingRecord)==1)
				{
					if(mainwindow.addGroup(frdList)==1)
					{
						welcome.loading.close();
						welcome.clear();
						welcome.close();
						mainwindow.show();
					}
					else
					{
						welcome.loading.close();
						welcome.alert1.exec(welcome, "加载好友列表失败，请重试");
					}
				}
				else
				{
					welcome.loading.close();
					welcome.alert1.exec(welcome, "加载消息列表失败，请重试");
				}
			}
		}
	}

	//run Login Operation
	public void Enter() throws IOException
	{
		((Button) welcome.search("Login")).setOnAction(event ->{
			String User = ((TextField) welcome.search("UserName")).getText();
			String Password = ((PasswordField) welcome.search("Password")).getText();
			boolean tf = true;
			for(int i=0; i<User.length(); ++i)
			{
				if(!(User.charAt(i)>='0' && User.charAt(i)<='9'))
				{
					tf = false;
					break;
				}
			}
			//System.out.println("U"+User);
			//System.out.println("P"+Password);
			if(User.equals(""))//Account is empty
			{
				welcome.ClrErrTip();
				welcome.setErrTip("ACCErr", "id不能为空");
			}
			else if(Password.equals(""))//Password is empty
			{
				welcome.ClrErrTip();
				welcome.setErrTip("PWErr", "密码不能为空");
			}
			else if(!tf)
			{
				welcome.ClrErrTip();
				welcome.setErrTip("ACCErr", "请输入数字id！");
			}
			else
			{
				welcome.ClrErrTip();
				welcome.loading.exec(welcome, "正在连接...");
				Task task = new Task<ChatBean>(){
					public ChatBean call() throws InterruptedException {
						 return SEND_ID_AND_PASSWORD_TO_SERVER(Integer.parseInt(User), Password);
					}
				};
				task.setOnSucceeded(taskFinishEnent ->{
					ChatBean state = (ChatBean)task.getValue();
					if(state == null)//fail to connect
					{
						welcome.loading.close();
						welcome.alert1.exec(welcome, "连接失败，请检查网络");
					}
					else if(state.type == TypeValue.REPLY_BAD_ID)//Account don't exist
					{
						welcome.loading.close();
						welcome.ClrErrTip();
						welcome.setErrTip("ACCErr", "账号不存在！");
					}
					else if(state.type == TypeValue.REPLY_CHECK_FAILED)
					{
						welcome.loading.close();
						welcome.ClrErrTip();
						welcome.setErrTip("PWErr", "密码错误！");
					}
					else if(state.type == TypeValue.REPLY_SERVER_ERROR)
					{
						welcome.loading.close();
						welcome.ClrErrTip();
						welcome.alert1.exec(welcome, "服务器发生异常，请稍后再试");
					}
					else if(state.type == TypeValue.REPLY_OK)
					{
						welcome.ClrErrTip();
						usrInfo = state.accountInfo;
						frdList = state.friendList;
						chattingRecord = state.chattingRecord;
						Logging();
					}
					else
					{
						welcome.loading.close();
						welcome.ClrErrTip();
						welcome.alert1.exec(welcome, "登录失败，请重试");
					}
				} );
				task.setOnCancelled(event1 -> {
					welcome.loading.close();
					welcome.alert1.exec(welcome, "发生错误，请重试");
				});
				task.setOnFailed(event1 -> {
					welcome.loading.close();
					welcome.alert1.exec(welcome, "发生错误，请重试");
				});
				new Thread(task).start();
			}
		});
	}

	/********************     Used in Register window   *******************/

	//save Register data into database
	public void Save()
	{
		((Button) register.search("Register")).setOnAction(event ->{
			String User = ((TextField) register.search("UserName")).getText();
			String Password = ((PasswordField) register.search("Password")).getText();
			String Affirm = ((PasswordField) register.search("Affirm")).getText();
			String MailAddr = ((TextField) register.search(("Mail"))).getText();
			LocalDate birth = ((DatePicker) register.search("Birthday")).getValue();
			//System.out.println(birth);
			String sex = "0";
			if(((RadioButton) register.search("boy")).isSelected())
				sex = "1";
			else if(((RadioButton) register.search("girl")).isSelected())
				sex = "2";
			//Condition: len(account)<=15; Pw only uses alpha, num, 6<=len<=20;
			String regPw = "^(?![a-zA-z]+$)(?!\\d+$)(?![!@#$%^&*.]+$)(?![a-zA-z\\d]+$)(?![a-zA-z!@#$%^&*]+$)(?![\\d!@#$%^&*]+$)[a-zA-Z\\d!@#$%^&*]+$";
			String regMail = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";
			if(User.equals("")||Password.equals("")||Affirm.equals("")||MailAddr.equals("")||birth==null)
			{
				register.ClrErrTip();
				if(User.equals(""))
					register.setErrTip("UserNameErr", "用户名不能为空");
				if (Password.equals(""))
					register.setErrTip("PasswordErr", "密码不能为空");
				if(Affirm.equals(""))
					register.setErrTip("AffirmErr", "请确认密码");
				if(MailAddr.equals(""))
					register.setErrTip("MailErr", "请输入邮箱");
				if(birth==null)
					register.setErrTip("DateErr", "请输入正确的日期格式");
			}
			else
			{
				register.ClrErrTip();
				if(User.length() > 15)
				{
					register.ClrErrTip();
					register.setErrTip("UserNameErr", "用户名不可超过15个字符");
				}
				else if(!Pattern.matches(regPw, Password))
				{
					register.ClrErrTip();
					register.setErrTip("PasswordErr", "密码须包含字母、数字、特殊字符");
				}
				else if(Password.length() > 20)
				{
					register.ClrErrTip();
					register.setErrTip("PasswordErr", "密码不能多于20个字符");
				}
				else if(Password.length() < 6)
				{
					register.ClrErrTip();
					register.setErrTip("PasswordErr", "密码不能少于6个字符");
				}
				else if(!Password.equals(Affirm))
				{
					register.ClrErrTip();
					register.setErrTip("AffirmErr", "两次输入的密码不一致");
				}
				else if(!Pattern.matches(regMail, MailAddr))
				{
					register.ClrErrTip();
					register.setErrTip("MailErr", "请输入正确的邮箱格式");
				}
				else
				{
					AccountInfo account = new AccountInfo();
					account.NickName = User; account.Birthday = birth.toString();
					account.Sex = sex; account.Mail = MailAddr;
					register.loading.exec(register, "正在注册...");
					Task task = new Task<Integer>(){
						public Integer call()
						{
							return SEND_NEW_ACCOUNT_TO_SERVER(account, Password);
						}
					};
					task.setOnSucceeded(taskFinishEvent ->{
						int new_id = ((Integer)task.getValue()).intValue();
						if(new_id == -1)//fail to connect
						{
							register.loading.close();
							register.alert1.exec(register, "连接异常，请检查网络");
						}
						else if(new_id == -2)
						{
							register.loading.close();
							register.alert1.exec(register, "服务器异常，请稍后重试");
						}
						else if(new_id > 0)
						{
							// Show affirmation window and go back to welcome window.
							register.loading.close();
							register.clear();
							register.close();
							welcome.show();
							register.alert.exec(welcome, "注册成功，您的id是："+ new_id);
							((TextField) welcome.search("UserName")).setText(""+new_id);
							((PasswordField) welcome.search("Password")).setText(Password);
						}
						else
						{
							register.loading.close();
							register.alert1.exec(register, "注册失败，请重试");
						}
					});
					task.setOnCancelled(event1 -> {
					    register.loading.close();
					    register.alert1.exec(register, "发生错误，请重试");
                    });
					task.setOnFailed(event1 -> {
                        register.loading.close();
                        register.alert1.exec(register, "发生错误，请重试");
                    });
                    new Thread(task).start();
				}
			}

		});
	}

	/********************     Used in Forget window   *******************/

	//get affirmation info from server, and send new password to server
	public void getCode()
	{
		((Button) forget.search("Get")).setOnAction(event ->{
			String User = ((TextField) forget.search("User")).getText();
			boolean yn = true;
			for(int i=0; i<User.length(); ++i)
			{
				if(!(User.charAt(i)>='0' && User.charAt(i)<='9'))
				{
					yn = false;
					break;
				}
			}
			if(User.equals(""))
			{
				forget.ClrErrTip();
				((Label) forget.search("UserErr")).setText("请输入用户id");
			}
			else if(!yn)
			{
				forget.ClrErrTip();
				((Label) forget.search("UserErr")).setText("请输入正确的用户id");
			}
			else
			{
				forget.loading.exec(forget, "正在发送请求，请稍候");
				Task task = new Task<Integer>(){
				    public Integer call()
                    {
                        return WHETHER_CAPTCHA_REQUEST_SEND_SUCCESSFULLY(Integer.parseInt(User));
                    }
                };
				task.setOnSucceeded(event1 -> {
                    int tf = ((Integer) task.getValue()).intValue();

                    if(tf == -1)//fail to connect
                    {
                        forget.loading.close();
                        forget.alert1.exec(forget, "连接失败，请重试");
                    }
                    else if(tf == -2)
                    {
                        forget.loading.close();
                        forget.alert1.exec(forget, "服务器错误，请稍后再试");
                    }
                    else if(tf == -3)
                    {
                        forget.loading.close();
                        forget.alert1.exec(forget, "获取失败，请重试");
                    }
                    else if(tf == 0)// wrong username
                    {
                        forget.loading.close();
                        ((Label) forget.search("UserErr")).setText("用户不存在！");
                    }
                    else //send successfully
                    {
                        forget.get ++;
                        forget.loading.close();
                        forget.alert.exec(forget, "获取成功,请检查邮箱");
                        ((Button) forget.search("Get")).setText("重新获取");
                    }
                });
				task.setOnFailed(event1 -> {
                    forget.loading.close();
                    forget.alert1.exec(forget, "发生错误，请重试");
                });
				task.setOnCancelled(event1 -> {
                    forget.loading.close();
                    forget.alert1.exec(forget, "发生错误，请重试");
                });
                new Thread(task).start();
			}
		});
	}
	// the logic of forget window
	public void Change()
	{
		((Button) forget.search("Reset")).setOnAction(event ->{
			String User = ((TextField) forget.search("User")).getText();
			String Code = ((TextField) forget.search("Check")).getText();
			String NewPw = ((PasswordField) forget.search("New")).getText();
			String Affirm = ((PasswordField) forget.search("Affirm")).getText();

			boolean yn = true;
			for(int i=0; i<User.length(); ++i)
			{
				if(!(User.charAt(i)>='0' && User.charAt(i)<='9'))
				{
					yn = false;
					break;
				}
			}

			if(User.equals("")||Code.equals("")||NewPw.equals("")||Affirm.equals(""))
			{
				forget.ClrErrTip();
				if(User.equals(""))
					((Label) forget.search("UserErr")).setText("用户名不能为空");
				if(Code.equals(""))
					((Label) forget.search("CheckErr")).setText("校验码不能为空");
				if(NewPw.equals(""))
					((Label) forget.search("NewErr")).setText("密码不能为空");
				if(Affirm.equals(""))
					((Label) forget.search("AffirmErr")).setText("请确认密码");
			}
			else
			{
				String regPw = "^(?![a-zA-z]+$)(?!\\d+$)(?![!@#$%^&*.]+$)(?![a-zA-z\\d]+$)(?![a-zA-z!@#$%^&*]+$)(?![\\d!@#$%^&*]+$)[a-zA-Z\\d!@#$%^&*]+$";
				if(!yn)
				{
					forget.ClrErrTip();
					((Label) forget.search("UserErr")).setText("请输入正确的用户id");
				}
				else if(forget.get==0)
				{
					forget.ClrErrTip();
					((Label) forget.search("CheckErr")).setText("您还未获取验证码！");
				}
				else if(!Pattern.matches(regPw, NewPw))
				{
					forget.ClrErrTip();
					((Label) forget.search("NewErr")).setText("密码只能包含字母，数字及特殊字符");
				}
				else if(NewPw.length() > 20)
				{
					forget.ClrErrTip();
					((Label) forget.search("NewErr")).setText("密码不能多于20个字符");
				}
				else if(NewPw.length() < 6)
				{
					forget.ClrErrTip();
					((Label) forget.search("NewErr")).setText("密码不能少于6个字符");
				}
				else if(!NewPw.equals(Affirm))
				{
					forget.ClrErrTip();
					((Label) forget.search("AffirmErr")).setText("两次输入的密码不一致");
				}
				else
				{
					forget.loading.exec(forget, "正在发送请求，请稍候...");
					Task task = new Task<Integer>(){
					  public Integer call()
                      {
                          return CHANGE_PASSWORD(Integer.parseInt(User), Code, NewPw);
                      }
                    };
					task.setOnSucceeded(event1 -> {
                        int state = ((Integer)task.getValue());
                        if(state == -1)// fail to connect
                        {
                            forget.loading.close();
                            forget.alert1.exec(forget, "连接失败，请检查网络");
                        }
                        else if(state == -2)
                        {
                            forget.loading.close();
                            forget.alert1.exec(forget, "服务器错误，请重试");
                        }
                        else if(state == -3)
                        {
                            forget.loading.close();
                            forget.alert1.exec(forget, "ID不正确，请重试");
                        }
                        else if(state == -4)
                        {
                            forget.loading.close();
                            forget.alert1.exec(forget, "修改失败，请重试");
                        }
                        else if(state == 0)// code is different between client and server
                        {
                            forget.loading.close();
                            forget.ClrErrTip();
                            ((Label) forget.search("CheckErr")).setText("校验码错误，请重新输入");
                        }
                        else
                        {
                            forget.get = 0;
                            forget.loading.close();
                            forget.clear();
                            forget.close();
                            welcome.show();
                            forget.alert.exec(welcome, "重置成功！");
                            ((TextField) welcome.search("UserName")).setText(User);
                            ((PasswordField) welcome.search("Password")).setText(NewPw);
                            ((Button)forget.search("Get")).setText("获取");
                        }
                    });
					task.setOnFailed(event1 -> {
					    forget.loading.close();
					    forget.alert1.exec(forget, "发生错误，请重试");
                    });
					task.setOnCancelled(event1 -> {
                        forget.loading.close();
                        forget.alert1.exec(forget, "发生错误，请重试");
                    });
					new Thread(task).start();
				}
			}
		});


	}

	/********************     Used in HomePage.       *******************/

	//update personal data
	public void Update()
	{
		((Button) homepage.search("finish")).setOnAction(event ->{
			String newUsr = ((TextField) homepage.search("account")).getText();
			String newSex = "";
			String date = ((TextField) homepage.search("birth")).getText();
			String oriPw = ((PasswordField) homepage.search("origin")).getText();
			String newEmail = ((TextField) homepage.search("email")).getText();
			String newPw = ((PasswordField) homepage.search("password")).getText();
			String Affirm = ((PasswordField) homepage.search("affirm")).getText();
			if(((RadioButton) homepage.search("boy")).isSelected())
				newSex = "1";
			else
				newSex = "2";
			String regMail = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";
			String regPw = "^(?![a-zA-z]+$)(?!\\d+$)(?![!@#$%^&*.]+$)(?![a-zA-z\\d]+$)(?![a-zA-z!@#$%^&*]+$)(?![\\d!@#$%^&*]+$)[a-zA-Z\\d!@#$%^&*]+$";
			String regDt = "^\\d{4}\\-\\d{1,2}\\-\\d{1,2}$";
			boolean nameErr = (newUsr.length()>20);
			boolean mailErr = (!newEmail.equals("") && !Pattern.matches(regMail, newEmail));
			boolean dateErr = !Pattern.matches(regDt, date );
			boolean pwErr = (!newPw.equals("")) && ((!Affirm.equals(newPw)) || (!Pattern.matches(regPw, newPw)));
			if(nameErr || mailErr || pwErr|| dateErr)
			{
				((Label) homepage.search("nickname")).setText(usrInfo.getNickName());
				((TextField) homepage.search("account")).setText(usrInfo.getNickName()+"("+usrInfo.id+")");
				((TextField) homepage.search("label")).setText(((Label) mainwindow.search("Signature")).getText());
				((TextField) homepage.search("birth")).setText(usrInfo.getBirthday());
				((TextField) homepage.search("email")).setText(usrInfo.Mail);
				if(nameErr)
				{
					homepage.alert1.exec(homepage, "用户名应不多于20个字符");
					homepage.alert1.show();
				}
				else if(dateErr)
				{
					homepage.alert1.exec(homepage, "请输入正确的日期格式");
					homepage.alert1.show();
				}
				else if(mailErr)
				{
					homepage.alert1.exec(homepage, "请输入正确的邮箱格式");
					homepage.alert1.show();
				}
				else if(pwErr)
				{
					if(!Affirm.equals(newPw))
					{
						homepage.alert1.exec(homepage, "两次输入密码不一致！");
						homepage.alert1.show();
					}
					else if(!Pattern.matches(regPw, newPw)|| newPw.length()>20)
					{
						homepage.alert1.exec(homepage, "密码格式错误！");
						homepage.alert1.show();
					}
				}
                ((PasswordField) homepage.search("origin")).setText("");
                ((PasswordField) homepage.search("password")).setText("");
                ((PasswordField) homepage.search("affirm")).setText("");
                ((Button) homepage.search("edit")).setVisible(true);
                ((Button) homepage.search("edit")).setManaged(true);
                homepage.setNoAction();
			}
			else
			{
				AccountInfo info = new AccountInfo();
				info.id = usrInfo.id; info.Sex = newSex; info.Mail = newEmail; info.Birthday = date;info.NickName = newUsr;
				homepage.loading.exec(homepage, "正在修改，请稍候...");

				Task task = new Task<ChatBean>(){
				    public ChatBean call()
                    {
                        if(newPw.equals(""))
                            return CHANGE_USR_INFO(info, usr_pw, usr_pw );
                        else
                            return CHANGE_USR_INFO(info, oriPw, newPw);
                    }
                };
				task.setOnSucceeded(event1 -> {
                    ChatBean state = (ChatBean)task.getValue();
                    if(state == null)
                    {
                        homepage.loading.close();
                        homepage.alert1.exec(homepage, "连接失败，请重试");
                    }
                    else if(state.type == TypeValue.REPLY_CHECK_FAILED)
                    {
                        homepage.loading.close();
                        homepage.alert1.exec(homepage, "密码错误，请重试");
                    }
                    else if(state.type == TypeValue.REPLY_SERVER_ERROR)
                    {
                        homepage.loading.close();
                        homepage.alert1.exec(homepage, "服务器错误，请稍后重试");
                    }
                    else if(state.type == TypeValue.REPLY_OK)
                    {
                        if(!newPw.equals(""))
                            usr_pw = newPw;
                        usrInfo = info;

                        homepage.loading.close();
                        homepage.loading.exec(homepage, "正在更新主页数据...");

                        ((Label) homepage.search("nickname")).setText(usrInfo.NickName+"\n"+"("+usrInfo.id+")");
                        ((TextField) homepage.search("account")).setText(usrInfo.NickName);
                        ((Label) mainwindow.search("Name")).setText(usrInfo.NickName);

                        if(usrInfo.Sex.equals("1"))
                        {
                            ((Label) homepage.search("sexb")).setVisible(true);
                            ((Label) homepage.search("sexb")).setManaged(true);
                            ((Label) homepage.search("sexg")).setVisible(false);
                            ((Label) homepage.search("sexg")).setManaged(false);
                        }
                        else if(usrInfo.Sex.equals("2"))
                        {
                            ((Label) homepage.search("sexg")).setVisible(true);
                            ((Label) homepage.search("sexg")).setManaged(true);
                            ((Label) homepage.search("sexb")).setVisible(false);
                            ((Label) homepage.search("sexb")).setManaged(false);
                        }

                        ((TextField) homepage.search("birth")).setText(usrInfo.Birthday);
                        ((TextField) homepage.search(("email"))).setText(usrInfo.Mail);

                        homepage.loading.close();
                        homepage.alert.exec(homepage, "修改成功！");
                        homepage.alert.show();
                    }
                    else
                    {
                        homepage.loading.close();
                        homepage.alert1.exec(homepage, "更新失败，请重试");
                    }
                   // if(state.type != TypeValue.REPLY_OK)
					//{
						((TextField) homepage.search("account")).setText(usrInfo.NickName);
						((TextField) homepage.search("email")).setText(usrInfo.Mail);
						((TextField) homepage.search("birth")).setText(usrInfo.Birthday);

					//}
                    ((PasswordField) homepage.search("origin")).setText("");
                    ((PasswordField) homepage.search("password")).setText("");
                    ((PasswordField) homepage.search("affirm")).setText("");
                    ((Button) homepage.search("edit")).setVisible(true);
                    ((Button) homepage.search("edit")).setManaged(true);
                    homepage.setNoAction();
                });
				task.setOnCancelled(event1 -> {
				    homepage.loading.close();
				    homepage.alert1.exec(homepage, "发生错误，请重试");
                    ((PasswordField) homepage.search("origin")).setText("");
                    ((PasswordField) homepage.search("password")).setText("");
                    ((PasswordField) homepage.search("affirm")).setText("");
                    ((Button) homepage.search("edit")).setVisible(true);
                    ((Button) homepage.search("edit")).setManaged(true);
                    homepage.setNoAction();
                });
				task.setOnFailed(event1 -> {
                    homepage.loading.close();
                    homepage.alert1.exec(homepage, "发生错误，请重试");
                    ((PasswordField) homepage.search("origin")).setText("");
                    ((PasswordField) homepage.search("password")).setText("");
                    ((PasswordField) homepage.search("affirm")).setText("");
                    ((Button) homepage.search("edit")).setVisible(true);
                    ((Button) homepage.search("edit")).setManaged(true);
                    homepage.setNoAction();
                });
                new Thread(task).start();
			}

		});
	}

	/********************     Used in Group Adding Page.       *******************/

	//add new group
	public void AddNewGroup()
	{
		((Button) addgroup.search("submit")).setOnAction(event -> {
			String name = ((TextField) addgroup.search("input")).getText();
			boolean tf = true;
			for(int i=0; i<Control.frdList.groupNum; ++i)
			{
				if(Control.frdList.Groups[i].GroupName.equals(name))
				{
					tf = false;
					break;
				}
			}
			if(!tf)
			{
				addgroup.alert1.exec(addgroup, "该分组已存在！");
			}
			else
			{
				addgroup.loading.exec(addgroup, "正在添加...");
				Task task = new Task<ChatBean>(){
				    public ChatBean call()
                    {
                        return SEND_NEW_GROUP_TO_SERVER(Control.usrInfo.id, name);
                    }
                };
				task.setOnSucceeded(event1 -> {
                    ChatBean state = (ChatBean)task.getValue();
                    if(state == null)
                    {
                        addgroup.loading.close();
                        addgroup.alert1.exec(addgroup, "连接失败，请检查网络！");
                    }
                    else if(state.type == TypeValue.REPLY_SERVER_ERROR)
                    {
                        addgroup.loading.close();
                        addgroup.alert1.exec(addgroup, "服务器错误，请稍后再试");
                    }
                    else if(state.type == TypeValue.REPLY_OK)
                    {
                        Control.frdList = state.friendList;
                        Control.mainwindow.addGroup(frdList);
                        addgroup.loading.close();
                        ((Button) addgroup.alert.search("Affirm")).setOnAction(event2 -> {
                            addgroup.alert.close();
                            addgroup.close();
                        });
                        ((Label) addgroup.alert.search("Info")).setText("添加成功");
                        addgroup.alert.show();
                    }
                    else
                    {
                        addgroup.loading.close();
                        addgroup.alert1.exec(addgroup, "添加失败，请重试");
                    }
                });
				task.setOnFailed(event1 -> {
				    addgroup.loading.close();
				    addgroup.alert1.exec(addgroup,"发生错误，请重试");
                });
				task.setOnCancelled(event1 -> {
                    addgroup.loading.close();
                    addgroup.alert1.exec(addgroup,"发生错误，请重试");
                });
				new Thread(task).start();
			}
		});
	}

	/********************     Used in Friend Searching Page.       *******************/

	//search friends
	public void addNew()
	{
		((Button) searchnew.search("Search")).setOnAction(event -> {
			String id_s = ((TextField) searchnew.search("Input")).getText();
			boolean tf = true;
			for(int i=0; i<id_s.length(); ++i)
			{
				if(!(id_s.charAt(i)>='0' && id_s.charAt(i)<='9'))
				{
					tf = false;
					break;
				}
			}

			if(!tf)
			{
				searchnew.clear();
				((Label) searchnew.search("err")).setText("请输入数字id");
			}
			else if(id_s.equals(""))
			{
				searchnew.clear();
				((Label) searchnew.search("err")).setText("请输入用户id");
			}
			else
			{
				((Label) searchnew.search("err")).setText("");
				int id = Integer.parseInt(id_s);
				searchnew.loading.exec(searchnew, "正在查询...");
				Task task = new Task<AccountInfo>(){
				  public AccountInfo call()
                  {
                      return GET_SEARCH_INFO_FROM_SERVER(id);
                  }
                };
				task.setOnSucceeded(event1 -> {
                    AccountInfo user = (AccountInfo)task.getValue();
                    searchnew.loading.close();
                    if(user == null)//fail to connect
                    {
                        searchnew.alert1.exec(searchnew, "连接失败，请重试");
                    }
                    else
                    {
                        searchnew.clear();
                        if(user.id == -1)//invalid id
                        {
                            searchnew.alert1.exec(searchnew, "该用户不存在！");
                        }
                        else if(user.id == 0)
                        {
                            searchnew.alert1.exec(searchnew, "服务器错误，请稍后再试");
                        }
                        else
                        {
                            try
                            {
                                searchnew.add(user);
                            }catch (IOException e)
                            {
                                e.printStackTrace();
                            }

                        }
                    }
                });
				task.setOnCancelled(event1 -> {
				    searchnew.loading.close();
				    searchnew.alert1.exec(searchnew, "发生错误，请重试");
                });
				task.setOnFailed(event1 -> {
                    searchnew.loading.close();
                    searchnew.alert1.exec(searchnew, "发生错误，请重试");
                });
				new Thread(task).start();
			}
		});
	}

	/********************     Used in Chat Page.       *******************/

	//send message
	public void sendMsg()
	{
		((Button) chat.search("Send")).setOnAction(event -> {
			String content = ((TextArea) chat.search("Input")).getText();
			if(!content.equals(""))
			{
				OneMessage mes = new OneMessage();
				mes.MessageText = content;

				if(chat.info.id != 0)
				{
					((Button) chat.search("Send")).setDisable(true);
				    Task task = new Task<ChatBean>(){
                        public ChatBean call()
                        {
                            return SEND_MES_TO_SERVER(usrInfo.id, chat.info.id, mes);
                        }
                    };
				    task.setOnSucceeded(event1 -> {
						((Button) chat.search("Send")).setDisable(false);
                        ChatBean back = (ChatBean)task.getValue();
                        if(back == null)//failed
                        {
                            chat.alert1.exec(chat, "网络连接异常，请重试");
                        }
                        else if(back.type==TypeValue.REPLY_BAD_ID)
                        {
                            chat.alert1.exec(chat, "用户id不存在");
                        }
                        else if(back.type == TypeValue.REPLY_CHECK_FAILED)
                        {
                            chat.alert1.exec(chat, "该用户已不是您的好友");
                        }
                        else if(back.type == TypeValue.REPLY_SERVER_ERROR)
                        {
                            chat.alert1.exec(chat, "服务器异常，请稍后再试");
                        }
                        else if(back.type == TypeValue.REPLY_OK)
                        {
                            ((TextArea) chat.search("Input")).clear();
                            for(int i=0; i<chattingRecord.FriendNumber; ++i)
                            {
                                if(chattingRecord.ChatRecord.get(i).FriendID == chat.info.id )
                                {
                                    chattingRecord.ChatRecord.get(i).ChatRecordwithOne.add(back.message);
                                    chattingRecord.ChatRecord.get(i).MessageNumberwithOne = chattingRecord.ChatRecord.get(i).ChatRecordwithOne.size();
                                }
                            }
							int index = -1;
							for(int i=0; i<Control.mainwindow.getFriendVector().size(); ++i)
							{
								if(Control.mainwindow.getFriendVector().get(i).info.id == Control.chat.info.id)
								{
									Control.mainwindow.getFriendVector().get(i).Signature.setText(content);
									index = i;
									break;
								}
							}
							if(index>=0)
							{
								Friendlist tmp = Control.mainwindow.getFriendVector().get(index);
								Control.mainwindow.getFriendVector().remove(index);
								Control.mainwindow.getFriendVector().add(0, tmp);
							}
                            chat.addRight(usrInfo.id, content, back.message.ID);
                        }
                        else
                        {
                            chat.alert1.exec(chat, "发送失败，请重试");
                        }
                    });
					task.setOnCancelled(event1 -> {
						((Button) chat.search("Send")).setDisable(false);
					    chat.alert1.exec(chat, "发生错误，请重试");
                    });
					task.setOnFailed(event1 -> {
						((Button) chat.search("Send")).setDisable(false);
					    chat.alert1.exec(chat, "发生错误，请重试");
                    });
					new Thread(task).start();
				}
				else
				{
					((TextArea) chat.search("Input")).clear();
					for(int i=0; i<chattingRecord.FriendNumber; ++i)
					{
						if(chattingRecord.ChatRecord.get(i).FriendID == chat.info.id )
						{
							chattingRecord.ChatRecord.get(i).ChatRecordwithOne.add(mes);
							chattingRecord.ChatRecord.get(i).MessageNumberwithOne = chattingRecord.ChatRecord.get(i).ChatRecordwithOne.size();
						}
					}
					int index = -1;
					for(int i=0; i<Control.mainwindow.getFriendVector().size(); ++i)
					{
						if(Control.mainwindow.getFriendVector().get(i).info.id == 0)
						{
							Control.mainwindow.getFriendVector().get(i).Signature.setText(content);
							index = i;
							break;
						}
					}
					if(index>=0)
					{
						Friendlist tmp = Control.mainwindow.getFriendVector().get(index);
						Control.mainwindow.getFriendVector().remove(index);
						Control.mainwindow.getFriendVector().add(0, tmp);
					}
					chat.addRight(usrInfo.id, content, mes.ID);
				}
			}
		});
	}

	/**
	 * 注册时使用，向server发送AccountInfo类，构建新账户信息，
	 * 这里为了写起来方便我直接把所有基本信息打包到AccountInfo里了，server端对应的提取需要的数据就行了
	 * @param account : Register Information
	 * @return 构建成功返回id，失败（用户名已被使用）返回0，连接失败返回-1，服务器出错返回-2
	 */
	public int SEND_NEW_ACCOUNT_TO_SERVER(AccountInfo account, String password)
	{
		ChatBean info = new ChatBean();
		info.accountInfo = account;
		info.type = TypeValue.REQ_REGISTER;
		info.password = password;
		try
		{
			ChatBean back = network.request(info);
			if(back.type == TypeValue.REPLY_OK)
				return back.ID;
			else if(back.type == TypeValue.REPLY_SERVER_ERROR)
				return -2;
			else
				return 0;
		}catch (Exception e)
		{
			e.printStackTrace();
			return -1;
		}
	}

	/**
	 * 向server发送获取验证码的请求
	 * 发送成功并且成功发送验证码到邮箱里，返回1，连接失败返回-1
	 * 发送验证码失败（用户不存在）返回0，服务器错误返回-2，其他错误返回-3
	 * @param usr_id 账号
	 * @return 状态信息
	 */
	public int WHETHER_CAPTCHA_REQUEST_SEND_SUCCESSFULLY(int usr_id)
	{
		ChatBean info = new ChatBean();
		info.type = TypeValue.REQ_FORGET_PASSWORD;
		info.ID = usr_id;
		try
		{
			ChatBean back = network.request(info);
			if(back.type == TypeValue.REPLY_OK)
				return 1;
			else if(back.type == TypeValue.REPLY_BAD_ID)
				return 0;
			else if(back.type == TypeValue.REPLY_SERVER_ERROR)
				return -2;
			else
				return -3;
		}catch(Exception e)
		{
			return -1;
		}
	}

	/**
	 * 向server发送修改请求,提供id，验证码，新密码
	 * 如果成功修改，返回1，连接失败，返回-1，验证码错误，返回0
	 * 服务器错误，返回-2，ID错误，返回-3，其他错误，-4
	 * @param usr_id 账号
	 * @param code 验证码
	 * @param Password 新密码
	 * @return 状态信息
	 */
	public int CHANGE_PASSWORD(int usr_id, String code, String Password)
	{
		ChatBean info = new ChatBean();
		info.type = TypeValue.REQ_CHECK_CAPTCHA;
		info.ID = usr_id;
		info.captcha = code;
		info.password = Password;
		try
		{
			ChatBean back = network.request(info);
			if(back.type == TypeValue.REPLY_OK)
				return 1;
			else if(back.type == TypeValue.REPLY_BAD_ID)
				return -3;
			else if(back.type == TypeValue.REPLY_CHECK_FAILED)
				return 0;
			else if(back.type == TypeValue.REPLY_SERVER_ERROR)
				return -2;
			else
				return -4;

		}catch (Exception e)
		{
			return -1;
		}
	}

	/**
	 * 登录时使用，向server发送id和密码，用于验证身份信息
	 * 注意我这里需要的是返回信息，这里直接返回ChatBean了
	 * 用null代表连接失败
	 * @param usr_id 账号
	 * @param password 密码
	 * @return 包含初始化信息的ChatBean
	 */
	public ChatBean SEND_ID_AND_PASSWORD_TO_SERVER(int usr_id, String password)
	{
		ChatBean info = new ChatBean();
		info.ID = usr_id;
		info.password = password;
		info.type = TypeValue.REQ_LOGIN;
		try
		{
			return network.request(info);
		}catch(Exception e)
		{
			return null;
		}
	}

	/**
	 * 修改个人资料时使用，获取当前用户的账号更新后的基本信息
	 * @param acc 账号欲修改信息
	 * @param oldpw 原密码
	 * @param newpw 新密码
	 * @return 如果连接失败请返回空引用，否则返回ChatBean
	 */
	public ChatBean CHANGE_USR_INFO(AccountInfo acc, String oldpw, String newpw)
	{
		ChatBean info = new ChatBean();
		info.ID = acc.id;
		info.accountInfo = acc;
		info.password = oldpw;
		info.newInfo = newpw;
		info.type = TypeValue.REQ_EDIT_INFO;
		try
		{
			return network.request(info);
		}catch (Exception e)
		{
			return null;
		}
	}

	/**
	 * 发送用户id和新分组名到server，server更新后返回新的ChatBean
	 * @param usr_id 账号
	 * @param name 分组名
	 * @return Chatbean，新信息，若发送失败，返回空引用
	 */
	public ChatBean SEND_NEW_GROUP_TO_SERVER(int usr_id, String name)
	{
		ChatBean info = new ChatBean();
		info.ID = usr_id;
		info.newInfo = name;
		info.type = TypeValue.REQ_BUILD_GROUP;
		try
		{
			return network.request(info);
		}catch (Exception e)
		{
			return null;
		}
	}

	/**
	 * 向server发送待查找的好友id，搜寻对应的用户信息
	 * 由于我们限制了唯一id，因此搜寻结果应该也是唯一的
	 * @param id 欲查找的用户id
	 * @return 搜寻到了返回对应的AccountInfo，用户不存在请返回id为-1的AccountInfo（因为id非负），连接失败返回null
	 */
	public AccountInfo GET_SEARCH_INFO_FROM_SERVER(int id)
	{
		ChatBean info = new ChatBean();
		info.type = TypeValue.REQ_GET_INFO;
		info.ID = id;
		try
		{
			ChatBean back = network.request(info);
			switch (back.type)
			{
				case REPLY_OK:return back.accountInfo;
				case REPLY_BAD_ID:AccountInfo bad = new AccountInfo();bad.id = -1;return bad;
				case REPLY_SERVER_ERROR: AccountInfo bad1 = new AccountInfo(); bad1.id = 0; return bad1;
				default: return null;
			}
		}catch (Exception e)
		{
			return null;
		}
	}

	/**
	 * 向server发送聊天信息，等待返回时间戳和消息id
	 * @param usr_id 用户id
	 * @param mes 消息内容
	 * @return 组装好的消息
	 */
	public ChatBean SEND_MES_TO_SERVER(int usr_id, int id, OneMessage mes)
	{
		ChatBean info = new ChatBean(TypeValue.REQ_CHAT);
		info.ID = usr_id;
		info.friendID = id;
		info.message = mes;
		try
		{
			return network.request(info);

		}catch (Exception e)
		{
			return null;
		}
	}


}