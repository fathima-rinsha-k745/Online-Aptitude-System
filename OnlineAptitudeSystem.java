import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.io.*;

class Question {
    String q,a,b,c,d,ans,subject;
    Question(String q,String a,String b,String c,String d,String ans,String subject){
        this.q=q; this.a=a; this.b=b; this.c=c; this.d=d; this.ans=ans; this.subject=subject;
    }
}

public class OnlineAptitudeSystem extends JFrame {

    ArrayList<Question> allQ = new ArrayList<>();
    int[] answers = new int[30];
    int[] visited = new int[30];

    JLabel qLabel,timerLabel,subjectLabel;
    JRadioButton A,B,C,D;
    ButtonGroup group;
    JButton[] palette = new JButton[30];

    int index=0,timeLeft=1800;
    javax.swing.Timer timer;

    Color bg = new Color(245,247,250);
    Color card = Color.WHITE;
    Color primary = new Color(41,128,185);
   Color answered = new Color(74,222,128);    // medium light green
Color notAnswered = new Color(248,113,113); // medium light red

Color current = new Color(245,158,11);    // warm amber

Color notVisited = new Color(203,213,225); // soft grey
    OnlineAptitudeSystem(){

        setTitle("Online Aptitude System");
        setSize(1350,750);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setBackground(bg);

        Arrays.fill(answers,-1);
        Arrays.fill(visited,0);

        loadRandomSubject("verbal");
        loadRandomSubject("quant");
        loadRandomSubject("logic");

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(primary);
        header.setBorder(BorderFactory.createEmptyBorder(12,20,12,20));

        subjectLabel = new JLabel("VERBAL");
        subjectLabel.setForeground(Color.WHITE);
        subjectLabel.setFont(new Font("Segoe UI",Font.BOLD,24));

        timerLabel = new JLabel("Time: 30:00");
        timerLabel.setForeground(Color.WHITE);
        timerLabel.setFont(new Font("Segoe UI",Font.BOLD,18));

        header.add(subjectLabel,BorderLayout.WEST);
        header.add(timerLabel,BorderLayout.EAST);
        add(header,BorderLayout.NORTH);

        JPanel cardPanel = new JPanel();
        cardPanel.setBackground(card);
        cardPanel.setBorder(BorderFactory.createEmptyBorder(40,60,40,60));
        cardPanel.setLayout(new BoxLayout(cardPanel,BoxLayout.Y_AXIS));

        qLabel = new JLabel("",SwingConstants.CENTER);
        qLabel.setFont(new Font("Segoe UI",Font.BOLD,26));
        qLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        cardPanel.add(qLabel);
        cardPanel.add(Box.createRigidArea(new Dimension(0,30)));

        JPanel optPanel = new JPanel(new GridLayout(4,1,15,15));
        optPanel.setBackground(card);

        A=new JRadioButton();
        B=new JRadioButton();
        C=new JRadioButton();
        D=new JRadioButton();

        JRadioButton[] arr={A,B,C,D};

        for(JRadioButton rb:arr){
            rb.setFont(new Font("Segoe UI",Font.PLAIN,18));
            rb.setBackground(new Color(240,245,255));
            rb.setBorder(BorderFactory.createEmptyBorder(10,15,10,15));
        }

        group=new ButtonGroup();
        group.add(A); group.add(B); group.add(C); group.add(D);

        optPanel.add(A); optPanel.add(B); optPanel.add(C); optPanel.add(D);
        cardPanel.add(optPanel);

        add(cardPanel,BorderLayout.CENTER);

        JPanel right = new JPanel(new BorderLayout());
        right.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        JPanel sectionPanel = new JPanel(new GridLayout(3,1,6,6));
        String[] subs={"VERBAL","QUANT","LOGICAL"};

        for(String s:subs){
            JButton b=new JButton(s);
            b.setBackground(primary);
            b.setForeground(Color.WHITE);
            b.setFont(new Font("Segoe UI",Font.BOLD,14));
            b.setFocusPainted(false);
            b.addActionListener(e->goSection(s));
            sectionPanel.add(b);
        }

        JPanel palettePanel = new JPanel(new GridLayout(6,5,10,10));

        for(int i=0;i<30;i++){
            int q=i;
            palette[i]=new JButton(""+(i+1));
            palette[i].setFont(new Font("Segoe UI",Font.BOLD,14));
            palette[i].setBackground(notVisited);

            palette[i].addActionListener(e->{
                save();
                index=q;
                load();
            });

            palettePanel.add(palette[i]);
        }

        right.add(sectionPanel,BorderLayout.NORTH);
        right.add(palettePanel,BorderLayout.CENTER);
        add(right,BorderLayout.EAST);

        JPanel footer = new JPanel();
        footer.setBackground(bg);

        JButton prev = new JButton("Previous");
        JButton next = new JButton("Next");
        JButton submit = new JButton("Submit");

        prev.setBackground(primary);
        next.setBackground(primary);
        submit.setBackground(new Color(39,174,96));

        prev.setForeground(Color.WHITE);
        next.setForeground(Color.WHITE);
        submit.setForeground(Color.WHITE);

        prev.addActionListener(e->{ save(); index--; load(); });
        next.addActionListener(e->{ save(); index++; load(); });
        submit.addActionListener(e->{ save(); finish(); });

        footer.add(prev);
        footer.add(next);
        footer.add(submit);

        add(footer, BorderLayout.SOUTH);

        startTimer();
        load();

        setLocationRelativeTo(null);
        setVisible(true);
    }

    // ⭐ RANDOM SUBJECT LOADER
    void loadRandomSubject(String sub){
        try{
            ArrayList<Question> temp = new ArrayList<>();
            Scanner sc=new Scanner(new File(sub+".txt"));

            while(sc.hasNextLine()){
                String[] p=sc.nextLine().split("\\|");
                temp.add(new Question(p[0],p[1],p[2],p[3],p[4],p[5],sub));
            }
            sc.close();

            Collections.shuffle(temp);   // ⭐ RANDOM SHUFFLE

            for(int i=0;i<10;i++) allQ.add(temp.get(i));   // ⭐ PICK 10 RANDOM

        }catch(Exception e){
            JOptionPane.showMessageDialog(this,"Missing file: "+sub);
        }
    }

    void goSection(String s){
        save();
        if(s.equals("VERBAL")) index=0;
        if(s.equals("QUANT")) index=10;
        if(s.equals("LOGICAL")) index=20;
        load();
    }

    void load(){
        if(index<0) index=0;
        if(index>=30) index=29;

        Question q=allQ.get(index);
        subjectLabel.setText(q.subject.toUpperCase());
        qLabel.setText("<html><center>"+(index+1)+". "+q.q+"</center></html>");

        A.setText(q.a); B.setText(q.b); C.setText(q.c); D.setText(q.d);
        group.clearSelection();

        if(answers[index]==0) A.setSelected(true);
        if(answers[index]==1) B.setSelected(true);
        if(answers[index]==2) C.setSelected(true);
        if(answers[index]==3) D.setSelected(true);

        visited[index]=1;
        updatePalette();
    }

    void save(){
        if(A.isSelected()) answers[index]=0;
        if(B.isSelected()) answers[index]=1;
        if(C.isSelected()) answers[index]=2;
        if(D.isSelected()) answers[index]=3;
    }

    void updatePalette(){
        for(int i=0;i<30;i++){
            if(visited[i]==0) palette[i].setBackground(notVisited);
            else if(answers[i]==-1) palette[i].setBackground(notAnswered);
            else palette[i].setBackground(answered);
        }
        palette[index].setBackground(current);
    }

    void startTimer(){
        timer=new javax.swing.Timer(1000,e->{
            timeLeft--;
            int m=timeLeft/60, s=timeLeft%60;
            timerLabel.setText("Time: "+String.format("%02d:%02d",m,s));
            if(timeLeft==0) finish();
        });
        timer.start();
    }

    void finish(){
        int score=0;
        for(int i=0;i<30;i++){
            Question q=allQ.get(i);
            String ans="";
            if(answers[i]==0) ans=q.a;
            if(answers[i]==1) ans=q.b;
            if(answers[i]==2) ans=q.c;
            if(answers[i]==3) ans=q.d;
            if(ans.equals(q.ans)) score++;
        }
        JOptionPane.showMessageDialog(this,"Final Score: "+score+"/30");
        System.exit(0);
    }

    public static void main(String[] args){
        new OnlineAptitudeSystem();
    }
}