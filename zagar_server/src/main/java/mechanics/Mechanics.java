package mechanics;

import main.ApplicationContext;
import main.Service;
import messageSystem.Message;
import messageSystem.MessageSystem;
import messageSystem.messages.LeaderBoardMsg;
import messageSystem.messages.ReplicateMsg;
import model.GameSessionImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import ticker.Tickable;

/**
 * Created by apomosov on 14.05.16.
 */
public class Mechanics extends Service implements Tickable {
  @NotNull
  private final static Logger log = LogManager.getLogger(Mechanics.class);

  public Mechanics() {
    super("mechanics");
  }

  @Override
  public void run() {
    log.info(getAddress() + " started");
    while(true){
      tick(60);
    }
  }

  @Override
  public void tick(long elapsedNanos) {
    try {
      Thread.sleep(elapsedNanos);
    } catch (InterruptedException e) {
      log.error(e);
      Thread.currentThread().interrupt();
      e.printStackTrace();
    }

//    log.info("Start replication");
    @NotNull MessageSystem messageSystem = ApplicationContext.instance().get(MessageSystem.class);
    Message message = new ReplicateMsg(this.getAddress());
    messageSystem.sendMessage(message);

//    log.info("Start getting leaders");
    Message leadersMessage = new LeaderBoardMsg(this.getAddress());
    messageSystem.sendMessage(leadersMessage);

    if (GameSessionImpl.getFoodGenerator() != null) {
      GameSessionImpl.getFoodGenerator().generate();
    }
    //execute all messages from queue
    messageSystem.execForService(this);
  }
}
