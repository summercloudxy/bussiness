package com.xy.bussiness.notification;

import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.*;
import java.net.URL;

@Service
public class WindowsNotification {

    TrayIcon trayIcon;

    public static void main(String[] args) throws AWTException, InterruptedException {
        if (SystemTray.isSupported()) {
            WindowsNotification nd = new WindowsNotification();
            nd.displayTray();
            Thread.sleep(1000L);
            nd.displayTray();
        } else {
            System.err.println("System tray not supported!");
        }
    }

//    @PostConstruct
    public void init() throws AWTException {
        SystemTray tray = SystemTray.getSystemTray();

        //If the icon is a file
//        URL resource = this.getClass().getResource("/static/煤炉.png");
//        ImageIcon icon = new ImageIcon(resource);
        Image image = Toolkit.getDefaultToolkit().createImage("icon.png");
        //Alternative (if the icon is on the classpath):
        //Image image = Toolkit.getDefaultToolkit().createImage(getClass().getResource("icon.png"));

        trayIcon = new TrayIcon(image, "Mercari");
        //Let the system resize the image if needed
        trayIcon.setImageAutoSize(true);
        //Set tooltip text for the tray icon
        trayIcon.setToolTip("Mercari demo");
        tray.add(trayIcon);
    }

    public void displayTray() throws AWTException {
        //Obtain only one instance of the SystemTray object
        SystemTray tray = SystemTray.getSystemTray();

        //If the icon is a file
        URL resource = this.getClass().getResource("/static/煤炉.png");
        ImageIcon icon = new ImageIcon(resource);
        Image image = Toolkit.getDefaultToolkit().createImage("icon.png");
        //Alternative (if the icon is on the classpath):
        //Image image = Toolkit.getDefaultToolkit().createImage(getClass().getResource("icon.png"));

        TrayIcon trayIcon = new TrayIcon(image, "Mercari");
        //Let the system resize the image if needed
        trayIcon.setImageAutoSize(true);
        //Set tooltip text for the tray icon
        trayIcon.setToolTip("Mercari demo");
        tray.add(trayIcon);

        trayIcon.displayMessage("Hello, World", "notification demo", TrayIcon.MessageType.INFO);
    }


    public void display(String title, String content) {
        trayIcon.displayMessage(title, content, TrayIcon.MessageType.INFO);
    }


}
