package Project;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Scanner;

public class ImageViewer extends JFrame {
    private int width;
    private int height;
    private int maxnum;
    private int whitespace;
    private int []image;
    private int []imagebit;
    private double widpix,heigpix;
    private String filename;
    private JLabel nl,fl,wl,hl,all;
    private String mn;
    private Color color[];
    private BufferedInputStream bis;
    private Scanner scan;
    private JButton button;
    private JFileChooser jfc;
    private DrawingPanel dp;
    private javax.swing.Timer timer;
    private int usingbit,discard;


    ImageViewer(){
        nl = new JLabel();
        fl = new JLabel();
        wl = new JLabel();
        hl = new JLabel();
        all= new JLabel();
        button = new JButton("Open Image");
        dp = new DrawingPanel();
        timer = new Timer   (100,new TimerListener());
        mn="";
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jfc=new JFileChooser();
                jfc.setCurrentDirectory(new java.io.File("./"));
                jfc.setDialogTitle("Open Image");
                if (jfc.showOpenDialog(button) == JFileChooser.APPROVE_OPTION) {
                    filename = jfc.getName(jfc.getSelectedFile());
                    System.out.println(filename);
                    widpix=0;
                    heigpix=0;
                    try {
                        bis = new BufferedInputStream(new FileInputStream(filename));
                        scan = new Scanner(new File(filename));
                        mn = getMagicNumber();
                        System.out.println(mn);
                        switch (mn){
                            case "P1":
                                decodeP1();
                                timer.start();
                                break;
                            case "P2":
                                decodeP2();
                                timer.start();
                                break;
                            case "P3":
                                decodeP3();
                                timer.start();
                                break;
                            case "P4":
                                decodeP4();
                                timer.start();
                                break;
                            case "P5":
                                decodeP5();
                                timer.start();
                                break;
                            case "P6":
                                decodeP6();
                                timer.start();
                                break;
                        }
                    }
                    catch (FileNotFoundException e1) {
                        e1.printStackTrace();
                    }
                    catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
        Toolkit tk = Toolkit.getDefaultToolkit();
        this.add(all);
        this.add(dp,BorderLayout.CENTER);
        this.add(button,BorderLayout.NORTH);
        this.setSize(((int) tk.getScreenSize().getWidth()),(int) tk.getScreenSize().getHeight());
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    private void setLabel(){
        nl.setText("Image name: "+filename);
        nl.setBounds(0,20, 300,30);
        nl.setFont(new Font("TimesRoman", Font.ITALIC, 18));
        fl.setText("File format: "+mn);
        fl.setBounds(0,50, 300,30);
        fl.setFont(new Font("TimesRoman", Font.ITALIC, 18));
        wl.setText("Image width: "+width);
        wl.setBounds(0,80, 300,30);
        wl.setFont(new Font("TimesRoman", Font.ITALIC, 18));
        hl.setText("Image height: "+height);
        hl.setBounds(0,110, 300,30);
        hl.setFont(new Font("TimesRoman", Font.ITALIC , 18));
        all.setBounds(width+10,50,300,200);
        all.add(nl);
        all.add(fl);
        all.add(wl);
        all.add(hl);
    }
    private void decodeP1(){
        mn = scan.next();
        width = scan.nextInt();
        height =scan.nextInt();
        System.out.println(width);
        System.out.println(height);
        image = new int[width*height];
        for(int i=0; i<width*height; i++){
           image[i] = (scan.nextInt());
        }
    }
    private void decodeP2(){
        mn = scan.next();
        width = scan.nextInt();
        height = scan.nextInt();
        maxnum = scan.nextInt();
        System.out.println(width);
        System.out.println(height);
        System.out.println(maxnum);
        image= new int[width*height];
        color=new Color[width*height];
        for(int i=0; i<width*height; i++){
            image[i] = scan.nextInt();
            color[i] = new Color(image[i],image[i],image[i]);
        }
    }
    private void decodeP3(){
        mn = scan.next();
        width = scan.nextInt();
        height = scan.nextInt();
        maxnum = scan.nextInt();
        System.out.println(width);
        System.out.println(height);
        System.out.println(maxnum);
        image= new int[width*height*3];
        color=new Color[width*height];
        for(int i=0; i<width*height*3; i++){
            image[i] = scan.nextInt();
        }
        int i=0;
        for(int j=0;j<width*height*3;j+=3){
            color[i] = new Color(image[j],image[j+1],image[j+2]);
            i++;
        }
    }
    private void decodeP4()throws IOException{
        skipWhitespace();
        width = readNumber();
        skipWhitespace();
        height = readNumber();
        skipWhitespace();
        image = new int[width*height];
        imagebit = new int[width*height*8];
        for(int i=1; i<width*height; i++){
            image[i]=bis.read();
        }
        int b=0;
        usingbit=0;
        discard=(8-(width%8));
        System.out.println(discard);
        for(int j = 0; j < width*height; j++) {
            for (int k = 0; k <= 7; k++) {
                imagebit[b] = ((image[j] >>> (7-k)) & 1);
                b++;
            }
            image[j]=imagebit[usingbit];
            usingbit++;
            if(width%8!=0) {
                if (usingbit % (width + discard) == 0) {
                    usingbit += discard;
                }
            }
        }
    }
    private void decodeP5() throws IOException{
        skipWhitespace();
        width = readNumber();
        skipWhitespace();
        height = readNumber();
        skipWhitespace();
        maxnum = readNumber();
        image = new int[width*height];
        color = new Color[width*height];
        for(int i=0; i<(width*height); i++){
            image[i]=bis.read();
            color[i] = new Color(image[i],image[i],image[i]);
        }
    }
    private void decodeP6() throws IOException{
        skipWhitespace();
        width = readNumber();
        skipWhitespace();
        height = readNumber();
        skipWhitespace();
        maxnum = readNumber();
        image = new int[width*height*3];
        color = new Color[width*height];
        for(int i=0; i<image.length; i++){
            image[i]=bis.read();
        }
        int i=0;
        for(int j=0;j<width*height*3;j+=3){
            color[i] = new Color(image[j],image[j+1],image[j+2]);
            i++;
        }
    }
    private String getMagicNumber() {
        byte [] magicNum = new byte[2];
        try {
            bis.read(magicNum);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String(magicNum);
    }
    private void skipWhitespace() {
        try {
            whitespace = bis.read();
            while(Character.isWhitespace(whitespace))
                whitespace = bis.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private int readNumber() {
        String wstr = "";
        try {
            while(!Character.isWhitespace(whitespace)) {
                wstr = wstr + (whitespace-'0');
                whitespace = bis.read();
            }
        }catch(IOException e2) {}
        System.out.println(wstr);
        return Integer.parseInt(wstr);
    }
    class DrawingPanel extends JPanel{
        public void paintComponent(Graphics g){
            super.paintComponent(g);
            if(mn.equals("P1") || mn.equals("P4")){
                for(int col=0; col<widpix; col++){
                    for(int row=0; row<heigpix; row++){
                        g.setColor(image[row*width+col]!=0 ? Color.BLACK : Color.WHITE);
                        g.fillRect(col,row,1,1);
                    }
                }
            }
            else{
                for(int col=0; col<widpix; col++){
                    for(int row=0; row<heigpix; row++){
                        g.setColor(color[row*width+col]);
                        g.fillRect(col,row,1,1);
                    }
                }
            }
        }
    }
    public class TimerListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            widpix+=((double) (width* 10) / 100);
            heigpix+=((double) (height* 10) / 100);
            if(heigpix>=height){
                widpix=width;
                heigpix=height;
                timer.stop();
                setLabel();
                 }
            dp.repaint();
        }
    }


    public static void main(String[] args){
            new ImageViewer();
    }
}