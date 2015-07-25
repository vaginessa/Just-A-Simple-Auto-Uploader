/**
@author Chaz Kerby
*/
package com.chazwarp.JustASimpleAutoUploader;

import java.awt.Desktop;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.TumblrApi;
import org.scribe.model.Token;
import org.scribe.oauth.OAuthService;

import com.chazwarp.JustASimpleAutoUploader.Helper.JasauFileHelper;
import com.chazwarp.JustASimpleAutoUploader.JFrame.MainWindow;
import com.chazwarp.JustASimpleAutoUploader.JFrame.PreferencesWindow;
import com.tumblr.jumblr.JumblrClient;

public class Main {

	static JFrame mainWindow = null;
	static JFrame prefWindow = null;
	final protected static String ConsumerKey = "UgjrWgFKKJ1JLA0Yvar8xd5AEMwihDRAgQ9FsEUCGsCpV2m4ui";
	final protected static String ConsumerSecret = "jqmjIK9KG3TjgpjXAZvnxrO43gGEy9zHH1L0Ei3jkL40EJCETK";
	public static String Token = "";
	public static String TokenSecret = "";
	public static JumblrClient currentClient = null;
	static OutputStream os = null;
	
	public static void main(String[] args) {
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
		//Consumer Key, Consumer Secret
		currentClient = new JumblrClient(ConsumerKey, ConsumerSecret);
		
		if(JasauFileHelper.TokenExists()) {
			try {
				JasauFileHelper.ReadTokenFromFile();
				SetClientToken();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		CreateNewWindow();
	}
	
	private static void CreateNewWindow() {
		mainWindow = MainWindow.CreateWindow();
		mainWindow.setVisible(true);
	}
	
	private static void SetClientToken() {
		if(Token != null && TokenSecret != null) {
			currentClient.setToken(Token, TokenSecret);
		}
	}
	
	private static void Login() {
		try {
			os = new FileOutputStream(JasauFileHelper.getSaveDirectory(JasauFileHelper.savePrefix, "OutputTemp"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		OAuthService service = new ServiceBuilder().provider(TumblrApi.class).apiKey(ConsumerKey).apiSecret(ConsumerSecret).callback("http://www.thebest404pageever.com/").debugStream(os).debug().build();
		Token requestToken = service.getRequestToken();
		String authUrl = service.getAuthorizationUrl(requestToken);
		
		try {
			JasauFileHelper.ReadFromTempFile();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		try {
			Desktop.getDesktop().browse(new URI(authUrl));
		} catch (IOException | URISyntaxException e2) {
			e2.printStackTrace();
		}
		
		try {
			JasauFileHelper.WriteTokenToFile();
		} catch (IOException e3) {
			e3.printStackTrace();
		}
		SetClientToken();
	}
	
	private static void OpenPrefWindow() {
		prefWindow = PreferencesWindow.CreateWindow();
		prefWindow.setVisible(true);
	}
	
	public static void ButtonClicked(String id) {
		if (id == "Login") Login();
		else if (id == "Pref") OpenPrefWindow();
	}
}