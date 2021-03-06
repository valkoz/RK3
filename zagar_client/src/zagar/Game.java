package zagar;

import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import zagar.auth.AuthClient;
import zagar.network.ServerConnectionSocket;
import zagar.network.packets.PacketEjectMass;
import zagar.network.packets.PacketMove;
import zagar.util.Reporter;
import zagar.view.Cell;
import zagar.view.Food;
import zagar.view.GameFrame;

import javax.swing.*;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.TimeUnit;

import static zagar.GameConstants.*;

public class Game {
  /**My data*/
  @NotNull
  public static int playerId;
  public static ConcurrentLinkedDeque<Cell> player = new ConcurrentLinkedDeque<>();
  public static int score;
  @NotNull
  public static String login = DEFAULT_LOGIN;
  @NotNull
  public static String serverToken;

  /**All GameSession data*/
  public static volatile Cell[] cells;
  public static volatile Food[] food;
  public static String[] leaderBoard;

  /**Other*/
  public static double zoom = 0.6;
  public static long fps = 60;
  public static int spawnPlayer = -1;
  public static boolean rapidEject;
  @NotNull
  public static GameState state = GameState.NOT_AUTHORIZED;
  @NotNull
  public String gameServerUrl;
  @NotNull
  public AuthClient authClient = new AuthClient();
  @NotNull
  public static ServerConnectionSocket socket;

  public Game() {
    this.gameServerUrl = "ws://" + (JOptionPane.showInputDialog(null, "Host", DEFAULT_GAME_SERVER_HOST + ":" + DEFAULT_GAME_SERVER_PORT));

    authenticate();

    this.spawnPlayer = 100;

    final WebSocketClient client = new WebSocketClient();
    this.socket = new ServerConnectionSocket();
    new Thread(() -> {
      try {
        client.start();
        URI serverURI = new URI(gameServerUrl + "/clientConnection");
        ClientUpgradeRequest request = new ClientUpgradeRequest();
        request.setHeader("Origin", "zagar.io");
        client.connect(socket, serverURI, request);

        socket.awaitClose(7, TimeUnit.DAYS);
      } catch (Throwable t) {
        t.printStackTrace();
      }
    }).start();
  }

  private void authenticate() {
    while (serverToken == null) {
      AuthOption authOption = chooseAuthOption();
      if (authOption == null) {
        return;
      }
      this.login = JOptionPane.showInputDialog(null, "Login", DEFAULT_LOGIN);
      String password = (JOptionPane.showInputDialog(null, "Password", DEFAULT_PASSWORD));
      if (login == null) {
        login = DEFAULT_LOGIN;
      }
      if (password == null) {
        password = DEFAULT_PASSWORD;
      }
      if (authOption == AuthOption.REGISTER) {
        if (!authClient.register(login, password)) {
          Reporter.reportFail("Register failed", "Register failed");
        }
      } else {
        serverToken = authClient.login(Game.login, password);
        if (serverToken == null) {
          Reporter.reportWarn("Login failed", "Login failed");
        }
      }
    }
  }

  @Nullable
  private AuthOption chooseAuthOption() {
    Object[] options = {AuthOption.LOGIN, AuthOption.REGISTER};
    int authOption = JOptionPane.showOptionDialog(null,
        "Choose authentication option",
        "Authentication",
        JOptionPane.YES_NO_CANCEL_OPTION,
        JOptionPane.QUESTION_MESSAGE,
        null,
        options,
        options[1]);

    if (authOption == 0) {
      return AuthOption.LOGIN;
    }
    if (authOption == 1) {
      return AuthOption.REGISTER;
    }
    return null;
  }

  public void tick() throws IOException {
    if (socket.session != null && player.size() > 0) {
      int newScore = 0;
      for (Cell c : player) {
        newScore += c.size;
      }

      score = newScore;

      if (socket.session.isOpen()) {
        float dx = (float) (GameFrame.mouseX - GameFrame.frame_size.width / 2);
        float dy = (float) (GameFrame.mouseY - GameFrame.frame_size.height / 2);
        (new PacketMove(dx, dy)).write(socket.session);

        if (rapidEject) {
          new PacketEjectMass().write();
        }
      }
    }
  }

  private enum AuthOption {
    REGISTER, LOGIN
  }

  public enum GameState {
    NOT_AUTHORIZED, AUTHORIZED
  }
}
