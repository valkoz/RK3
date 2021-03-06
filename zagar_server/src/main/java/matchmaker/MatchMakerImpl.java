package matchmaker;

import model.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import utils.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Creates {@link GameSession} for single player
 *
 * @author Alpi
 */
public class MatchMakerImpl implements MatchMaker {
  @NotNull
  private final Logger log = LogManager.getLogger(MatchMakerImpl.class);
  @NotNull
  private final List<GameSession> activeGameSessions = new ArrayList<>();

  /**
   * Creates new GameSession for single player
   *
   * @param player single player
   */
  @Override
  public void joinGame(@NotNull Player player) {
    //if(activeGameSessions.)
    if(activeGameSessions.size() == 0) {
      GameSession newGameSession = createNewGame();
      activeGameSessions.add(newGameSession);
    }
    int i;
    for(i = 0; i < activeGameSessions.size(); ++i) {
      if(activeGameSessions.get(i).getPlayers().size() < 10) {
        activeGameSessions.get(i).join(player);
        break;
      }
    }
    if(i == activeGameSessions.size()) {
      GameSession newGameSession = createNewGame();
      newGameSession.join(player);
      activeGameSessions.add(newGameSession);
    }
    //GameSession newGameSession = createNewGame();
   // activeGameSessions.add(newGameSession);
    //activeGameSessions.get(0).join(player);//newgamesession
    if (log.isInfoEnabled()) {
      log.info(player + " joined " + activeGameSessions.get(0));
    }
  }

  @Override
  public void leaveGame(@NotNull Player player){
    getGameSession(player.getName()).leave(player);
  }

  @Override
  public GameSession getGameSession(String name){
    for (GameSession session : activeGameSessions){
      for (Player player : session.getPlayers()){
        if (player.getName().equals(name)) {
          return session;
        }
      }
    }
    return null;
  }

  @NotNull
  public List<GameSession> getActiveGameSessions() {
    return new ArrayList<>(activeGameSessions);
  }

  /**
   * @return new GameSession
   */
  private
  @NotNull
  GameSession createNewGame() {
    PlayerPlacer playerPlacer = new RandomPlayerPlacer();
    VirusGenerator virusGenerator = new RandomVirusGenerator(GameConstants.NUMBER_OF_VIRUSES);
    UniformFoodGenerator foodGenerator = new UniformFoodGenerator();

    return new GameSessionImpl(foodGenerator, playerPlacer, virusGenerator);
  }
}
