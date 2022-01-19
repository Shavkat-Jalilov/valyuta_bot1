import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

public class shavkat extends TelegramLongPollingBot{

    @Override
    public void onUpdateReceived(Update update) {

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
