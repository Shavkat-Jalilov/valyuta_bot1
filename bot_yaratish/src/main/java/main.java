import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.xml.internal.ws.server.ServerRtException;
import lombok.SneakyThrows;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

public class main extends TelegramLongPollingBot {
    public static void main(String[] args) {
        ApiContextInitializer.init();
        TelegramBotsApi api=new TelegramBotsApi();
         try {
             api.registerBot(new main());
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }
    }
    String message="";
    double dif1,dif2,dif3;
    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        System.out.println(update);
        ReplyKeyboardMarkup replay=new ReplyKeyboardMarkup().setOneTimeKeyboard(true).setResizeKeyboard(true).setSelective(true).setKeyboard(null);
        AtomicReference<String> matn= new AtomicReference<>(update.getMessage().getText());
        SendMessage send=new SendMessage().setChatId(update.getMessage().getChatId()).setReplyMarkup(replay);
        Gson gson=new GsonBuilder().setPrettyPrinting().create();

        URL url=new URL("https://cbu.uz/oz/arkhiv-kursov-valyut/json/");
        URLConnection urlCon=url.openConnection();
       // String btn_name="";
        final String[] btn_name=new String[4];
        final int[] index=new int[1];
        BufferedReader reader=new BufferedReader(new InputStreamReader(urlCon.getInputStream()));
        Type type=new TypeToken<ArrayList<MB>>(){}.getType();
        ArrayList<MB> mbs=gson.fromJson(reader,type);
        mbs.forEach(mb1 -> {
            if(mb1.getCcy().equals("USD")){
                dif1=Double.parseDouble(mb1.getDiff());
            }
            if (mb1.getCcy().equals("RUB")){
                dif2=Double.parseDouble(mb1.getDiff());
            }
            if(mb1.getCcy().equals("EUR")){
                dif3=Double.parseDouble(mb1.getDiff());
            }
        });
        String matn1=update.getMessage().getText();

        ArrayList<KeyboardRow> rows=new ArrayList<>();
        int tugma=0;
        if(matn1.equals("/start")){
            String nik=update.getMessage().getChat().getUserName();
            if(nik==null){
                nik=update.getMessage().getChat().getFirstName();
                send.setText("Assalomu alaykum "+nik+ "\n\nXush kelibsiz.\n\nSiz ushbu botda valyuta kurslari haqida xabardor bo'lishingiz va convertatsiya qilishingiz mumkin.");
            }
            else{
                send.setText("Assalomu alaykum @"+nik+ "\n\nXush kelibsiz.\n\nSiz ushbu botda valyuta kurslari haqida xabardor bo'lishingiz va convertatsiya qilishingiz mumkin.");
            }
            KeyboardRow row=new KeyboardRow();
            row.add("/Convertatsiya");
            rows.add(row);
        }
         else if(matn1.equals("/Convertatsiya")){
            send.setText("Convert turini tanlang");
            KeyboardRow row1=new KeyboardRow();
            KeyboardRow row2=new KeyboardRow();
            KeyboardRow row3=new KeyboardRow();
            row1.add("UZStoUSD");
            row1.add("USDtoUZS");
            row2.add("UZStoRUB");
            row2.add("RUBtoUZS");
            row3.add("UZStoEUR");
            row3.add("EURtoUZS");
            rows.add(row1);
            rows.add(row2);
            rows.add(row3);
        }
         else if(matn1.equals("UZStoUSD")){
           message="UZStoUSD";
            mbs.forEach(mb1 -> {
                if(mb1.getCcy().equals("USD")) {
                    if(dif1>0){
                        send.setText(mb1.getRate() + " UZS="+ mb1.getNominal()+" USD\nKechagi narxdan "+mb1.getDiff()+" so'mga oshdi.\n\n So'mmani kiriting: ");
                    }
                    else if(dif1==0){
                        send.setText(mb1.getRate() + " UZS="+ mb1.getNominal()+" USD\nKechagi narxga nisbatan o'zgarmagan.\n\n So'mmani kiriting: ");
                    }
                    else{
                        send.setText(mb1.getRate() + " UZS="+ mb1.getNominal()+" USD\nKechagi narxdan "+mb1.getDiff()+" so'mga pasaydi.\n\n So'mmani kiriting: ");

                    }
                }
            });

        }
         else if(message.equals("UZStoUSD")){
             int sum=Integer.parseInt(matn1);
             mbs.forEach(mb -> {
                 if (mb.getCcy().equals("USD")) {
                     if(dif1>0){
                         send.setText(matn1 + " UZS= " + Double.parseDouble(matn1) / Double.parseDouble(mb.getRate()) + " USD\n\nDollar kursi kechagiga nisbatan "+dif1+" so'mga oshgan.");
                     }
                    else if(dif1==0){
                         send.setText(matn1 + " UZS= " + Double.parseDouble(matn1) / Double.parseDouble(mb.getRate()) + " USD\n\nDollar kursi kechagiga nisbatan o'zgarmagan!");
                     }
                    else{
                         send.setText(matn1 + " UZS= " + Double.parseDouble(matn1) / Double.parseDouble(mb.getRate()) + " USD\n\nKiritgan so'mmangizdan kechagiga nisbatan "+sum*dif1+" so'mga foyda ko'rasiz.");
                     }
                 }
             });
             message="";
        }
        else if(matn1.equals("USDtoUZS")){
            message="USDtoUZS";
            mbs.forEach(mb1 -> {
                if(mb1.getCcy().equals("USD")) {
                    if(dif1>0){
                        send.setText(mb1.getNominal()+" USD=" + mb1.getRate() + " UZS\nKechagi narxdan "+mb1.getDiff()+" so'mga oshdi.\n\n So'mmani kiriting: ");
                    }
                    else if(dif1==0){
                        send.setText(mb1.getNominal()+" USD=" + mb1.getRate() + " UZS\nKechagi narxga nisbatan o'zgarmagan.\n\n So'mmani kiriting: ");
                    }
                    else{
                        send.setText(mb1.getNominal()+" USD=" + mb1.getRate() + " UZS\nKechagi narxdan "+mb1.getDiff()+" so'mga pasaydi.\n\n So'mmani kiriting: ");
                    }
                }
            });
        }
        else if(message.equals("USDtoUZS")){
            int sum=Integer.parseInt(matn1);
            mbs.forEach(mb -> {
                if (mb.getCcy().equals("USD")) {
                    if(dif1>0){
                        send.setText(matn1 + " USD= " + Double.parseDouble(matn1) * Double.parseDouble(mb.getRate()) + " UZS\n\nKiritgan so'mmangizdan kechagiga nisbatan "+sum*dif1+" so'mga foyda ko'rasiz.");
                    }
                    else if (dif1==0){
                        send.setText(matn1 + " USD= " + Double.parseDouble(matn1) * Double.parseDouble(mb.getRate()) + " UZS\n\nDollar kursi kechagiga nisbatan o'zgarmagan !");
                    }
                    else {
                        send.setText(matn1 + " USD= " + Double.parseDouble(matn1) * Double.parseDouble(mb.getRate()) + " UZS\n\nDollar kursi kechagiga nisbatan "+dif1+" so'mga kamaygan.");

                    }
                }
            });
            message="";
        }

        else if(matn1.equals("UZStoRUB")){
            message="UZStoRUB";
            mbs.forEach(mb1 -> {
                if(mb1.getCcy().equals("RUB")) {
                    if(dif2>0){
                        send.setText(mb1.getRate() + " UZS="+mb1.getNominal()+" RUB\nKechagi narxdan "+mb1.getDiff()+" so'mga oshdi.\n\n So'mmani kiriting: ");
                    }
                    else if(dif2==0){
                        send.setText(mb1.getRate() + " UZS="+mb1.getNominal()+" RUB\nKechagi narxga nisbatan o'zgarmagan.\n\n So'mmani kiriting: ");
                    }
                    else{
                        send.setText(mb1.getRate() + " UZS="+mb1.getNominal()+" RUB\nKechagi narxdan "+mb1.getDiff()+" so'mga pasaydi.\n\n So'mmani kiriting: ");
                    }
                }
            });
        }
        else if(message.equals("UZStoRUB")){
            int sum=Integer.parseInt(matn1);
            mbs.forEach(mb -> {
                if (mb.getCcy().equals("RUB")) {
                    if(dif2>0){
                        send.setText(matn1 + " UZS= " + sum / Double.parseDouble(mb.getRate()) + " RUB\n\nRublning kursi kechagiga nisbatan"+dif2+" so'mga ko'tarilgan.");
                    }
                    else if (dif2==0){
                        send.setText(matn1 + " UZS= " + sum / Double.parseDouble(mb.getRate()) + " RUB\n\nRubil kursi kechagiga nisbatan o'zgarmagan!");
                    }
                    else{
                        send.setText(matn1 + " UZS= " + sum / Double.parseDouble(mb.getRate()) + " RUB\n\nKiritgan so'mmangizdan kechagiga nisbatan "+sum*dif2+" so'm foyda ko'rasiz.");
                    }
                }
            });
            message="";
        }
        else if(matn1.equals("RUBtoUZS")){
            message="RUBtoUZS";
            mbs.forEach(mb1 -> {
                if(mb1.getCcy().equals("RUB")) {
                    if(dif2>0){
                        send.setText(mb1.getNominal() + " RUB="+mb1.getRate()+" UZS\nKechagi narxdan "+mb1.getDiff()+" so'mga oshdi.\n\n So'mmani kiriting: ");
                    }
                    else if(dif2==0){
                        send.setText(mb1.getNominal() + " RUB="+mb1.getRate()+" UZS\nKechagi narxga nisbatan o'zgarmagan.\n\n So'mmani kiriting: ");
                    }
                    else{
                        send.setText(mb1.getNominal() + " RUB="+mb1.getRate()+" UZS\nKechagi narxdan "+mb1.getDiff()+" so'mga pasaydi.\n\n So'mmani kiriting: ");
                    }
                }
            });
        }
        else if(message.equals("RUBtoUZS")){
            int sum=Integer.parseInt(matn1);
            mbs.forEach(mb -> {
                if (mb.getCcy().equals("RUB")) {
                    if(dif2>0){
                        send.setText(matn1 + " RUB= " + sum * Double.parseDouble(mb.getRate()) + " UZS\n\nKiritgan so'mmangizdan kechagiga nisbatan "+dif2*sum+" so'm foyda ko'rasiz.");
                    }
                    else if(dif2==0){
                        send.setText(matn1 + " RUB= " + sum * Double.parseDouble(mb.getRate()) + " UZS\n\nRubil kursi kechagiga nisbatan o'zgarmagan!");
                    }
                    else {
                        send.setText(matn1 + " RUB= " + sum * Double.parseDouble(mb.getRate()) + " UZS\n\nRubil kursi kechagiga nisbatan "+dif2+" so'mga kamaygan.");
                    }
                }
            });
            message="";
        }
        else if(matn1.equals("UZStoEUR")){
            message="UZStoEUR";
            mbs.forEach(mb1 -> {
                if(mb1.getCcy().equals("EUR")) {
                    if(dif3>0){
                        send.setText(mb1.getRate() + " UZS="+mb1.getNominal()+" EUR\nKechagi narxdan "+mb1.getDiff()+" so'mga oshdi.\n\n So'mmani kiriting: ");
                    }
                    else if(dif3==0){
                        send.setText(mb1.getRate() + " UZS="+mb1.getNominal()+" EUR\nKechagi narxga nisbatan o'zgarmagan.\n\n So'mmani kiriting: ");
                    }
                    else{
                        send.setText(mb1.getRate() + " UZS="+mb1.getNominal()+" EUR\nKechagi narxdan "+mb1.getDiff()+" so'mga pasaydi.\n\n So'mmani kiriting: ");
                    }
                }
            });
        }
        else if(message.equals("UZStoEUR")){
            int sum=Integer.parseInt(matn1);
            mbs.forEach(mb -> {
                if (mb.getCcy().equals("EUR")) {
                    if(dif3>0){
                        send.setText(matn1 + " UZS= " + sum / Double.parseDouble(mb.getRate()) + " EUR\n\nYevro ning kursi kechagiga nisbatan"+dif2+" so'mga ko'tarilgan.");
                    }
                    else if (dif3==0){
                        send.setText(matn1 + " UZS= " + sum / Double.parseDouble(mb.getRate()) + " EUR\n\nYevro ning kursi kechagiga nisbatan o'zgarmagan!");
                    }
                    else{
                        send.setText(matn1 + " UZS= " + sum / Double.parseDouble(mb.getRate()) + " EUR\n\nKiritgan so'mmangizdan kechagiga nisbatan "+sum*dif2+" so'm foyda ko'rasiz.");
                    }
                }
            });
            message="";
        }
        else if(matn1.equals("EURtoUZS")){
            message="EURtoUZS";
            mbs.forEach(mb1 -> {
                if(mb1.getCcy().equals("EUR")) {
                    if(Double.parseDouble(mb1.getDiff().toLowerCase())>0){
                        send.setText(mb1.getNominal() + " EUR="+mb1.getRate()+" UZS\nKechagi narxdan "+mb1.getDiff()+" so'mga oshdi.\n\n So'mmani kiriting: ");
                    }
                    else if(Double.parseDouble(mb1.getDiff().toLowerCase())==0){
                        send.setText(mb1.getNominal() + " EUR="+mb1.getRate()+" UZS\nKechagi narxga nisbatan o'zgarmagan.\n\n So'mmani kiriting: ");

                    }
                    else{
                        send.setText(mb1.getNominal() + " EUR="+mb1.getRate()+" UZS\nKechagi narxdan "+mb1.getDiff()+" so'mga pasaydi.\n\n So'mmani kiriting: ");
                    }
                }
            });
        }
        else if(message.equals("EURtoUZS")){
            int sum=Integer.parseInt(matn1);
            mbs.forEach(mb -> {
                if (mb.getCcy().equals("EUR")) {
                    if(dif3>0){
                        send.setText(matn1 + " EUR= " + sum * Double.parseDouble(mb.getRate()) + " UZS\n\nKiritgan so'mmangizdan kechagiga nisbatan "+dif2*sum+" so'm foyda ko'rasiz.");
                    }
                    else if(dif3==0){
                        send.setText(matn1 + " EUR= " + sum * Double.parseDouble(mb.getRate()) + " UZS\n\nYevro kursi kechagiga nisbatan o'zgarmagan!");
                    }
                    else {
                        send.setText(matn1 + " EUR= " + sum * Double.parseDouble(mb.getRate()) + " UZS\n\nYevro kursi kechagiga nisbatan "+dif2+" so'mga kamaygan.");
                    }
                }
            });
            message="";
        }

        replay.setKeyboard(rows);
        try {
            execute(send);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    @Override
    public String getBotUsername() {
        return "jalilovshavkat_bot";
    }

    @Override
    public String getBotToken() {
        return "5016963577:AAFC8jru_fMtYH5dpm3hFcLzGKxzxON-pXc";
    }
}
