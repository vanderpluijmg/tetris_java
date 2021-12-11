/*
 * MIT License
 *
 * Copyright (c) 2021 Andrew SASSOYE, Constantin GUNDUZ, Gregory VAN DER PLUIJM, Thomas LEUTSCHER
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package esi.acgt.atlj.client.model;

import esi.acgt.atlj.client.model.game.MultiplayerGame;
import esi.acgt.atlj.message.AbstractMessage;
import esi.acgt.atlj.message.GameMessage;
import esi.acgt.atlj.message.ServerRequest;
import esi.acgt.atlj.message.StatisticMessage;
import esi.acgt.atlj.message.messageTypes.AskPiece;
import esi.acgt.atlj.message.messageTypes.Connection;
import esi.acgt.atlj.message.messageTypes.GameStatAction;
import esi.acgt.atlj.message.messageTypes.Hold;
import esi.acgt.atlj.message.messageTypes.LockTetrimino;
import esi.acgt.atlj.message.messageTypes.PlayerState;
import esi.acgt.atlj.message.messageTypes.Request;
import esi.acgt.atlj.message.messageTypes.SendStartGame;
import esi.acgt.atlj.message.messageTypes.SetFallingTetrimino;
import esi.acgt.atlj.model.Game;
import esi.acgt.atlj.model.player.Action;
import esi.acgt.atlj.model.player.Direction;
import esi.acgt.atlj.model.player.PlayerStatus;
import esi.acgt.atlj.model.tetrimino.Mino;
import esi.acgt.atlj.model.tetrimino.TetriminoInterface;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.ConnectException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.input.KeyCode;


/**
 * Sets up a specialized client with overridden methods from AbstractClient on which application
 * will run on.
 *
 * @see AbstractClient
 */
public class Client extends AbstractClient implements ClientInterface, PropertyChangeListener {

  private static final Logger logger = Logger.getLogger(Client.class.getName());

  private ClientStatus status;

  private HashMap<String, Integer> stats;

  private final String username;

  private Game game;

  /**
   * Constructor of a client.
   *
   * @param port Port client must look for.
   * @param host Host client must connect to.
   */
  public Client(int port, String host, String username) throws ConnectException {
    super(port, host);
    this.status = ClientStatus.CONNECTED;
    this.username = username;
    this.stats = new HashMap<>();
    connect();
    sendNameToServer(username);
  }

  public String getUsername() {
    return this.username;
  }

  public HashMap<String, Integer> getStats() {
    return this.stats;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void handleServerMessage(Object information) {
    AbstractMessage message = (AbstractMessage) (information);
    logger.log(Level.INFO,
        String.format("Received %s message type from server", message.getType().name()));
    switch (message.getType()) {
      case NEW_GAME -> ((SendStartGame) message).execute(game);
      case GAME -> ((GameMessage) message).execute(game);
      case STATISTICS -> ((StatisticMessage) message).execute(stats);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void closeConnectionToServer() {
    super.closeConnectionToServer();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void notifyLoss() {
    try {
      sendToServer(new Object()); //todo :)
    } catch (IOException e) {
      System.err.println("Sorry cannot send loss");
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void sendTetriminoToOtherPlayer(TetriminoInterface tetriminoInterface) {
    try {
      sendToServer(new SetFallingTetrimino(tetriminoInterface, this.username));
    } catch (IOException e) {
      System.err.println("Cannot send tetrimino to server");
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void connectionEstablished() {
    System.out.println("Connection to server is established");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void closeConnection() {
    System.err.println("Connection to server has been suspended");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void connexionException(Exception e) {

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void connect() throws ConnectException {
    connectToServer();
  }

  @Override
  public void askAllStatistics() {
    try {
      sendToServer(new Request(ServerRequest.GET_STATS));
      logger.log(Level.INFO,
          "Successfully requested stats to server");
    } catch (IOException e) {
      logger.log(Level.SEVERE, e.getMessage());
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void requestNextMino() {
    try {
      sendToServer(new AskPiece());
      logger.log(Level.INFO,
          "Successfully requested a mino to server");
    } catch (IOException e) {
      logger.log(Level.SEVERE, e.getMessage());
    }
  }

  /**
   * {@inheritDoc}
   *
   * @param a Action to send.
   */
  @Override
  public void sendAction(ServerRequest a) {
    try {
      Request action = new Request(a);
      sendToServer(action);
      logger.log(Level.INFO,
          String.format("Successfully sent action to server (%s)", a.name()));
    } catch (IOException e) {
      logger.log(Level.SEVERE, e.getMessage());
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void sendHoldMino(Mino m) {
    try {
      sendToServer(new Hold(m, this.username));
      logger.log(Level.INFO,
          String.format("Successfully sent hold tetrimino to server (%s)", m.name()));
    } catch (IOException e) {
      logger.log(Level.SEVERE, e.getMessage());
    }
  }

  @Override
  public void lockTetrimino(TetriminoInterface m) {
    try {
      sendToServer(new LockTetrimino(m, this.username));
      logger.log(Level.INFO,
          String.format("Successfully sent locked tetrimino to server (%s)", m.getType().name()));
    } catch (IOException e) {
      logger.log(Level.SEVERE, e.getMessage());
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void sendNameToServer(String name) {
    try {
      sendToServer(new Connection(name));
      logger.log(Level.INFO, String.format("Successfully sent username to server (%s)", name));
    } catch (IOException e) {
      logger.log(Level.SEVERE, e.getMessage());
    }
  }

  /**
   * {@inheritDoc}
   *
   * @param action
   */
  @Override
  public void sendStatAction(Action action) {
    if (action == Action.SOFT_DROP || action == Action.HARD_DROP) {
      try {
        sendToServer(new GameStatAction(action, this.username));
        logger.log(Level.INFO,
            String.format("Successfully sent stat action to server (%s)", action.name()));
      } catch (IOException e) {
        System.err.println("Cannot send action to server");
      }
    }
  }

  @Override
  public void startMultiplayerGame(String username, PropertyChangeListener[] listeners) {
    this.game = new MultiplayerGame(username);
    game.addPropertyChangeListenerToBoards(listeners);
    ((MultiplayerGame) this.game).addPCSToPlayer(this);
    logger.log(Level.INFO,
        String.format("Successfully initiated multiplayer game for %s with %d listeners", username,
            listeners.length));

    sendAction(ServerRequest.MULTIPLAYER);
  }

  @Override
  public void addPCSToBoard(PropertyChangeListener[] listeners) {
    if (game != null) {
      game.addPropertyChangeListenerToBoards(listeners);
    }
  }

  public void sendStatusUpdate(PlayerStatus status) {
    try {
      sendToServer(new PlayerState(this.username, status));
      logger.log(Level.INFO,
          String.format("Successfully sent status update to server (%s)", status.name()));
    } catch (IOException e) {
      logger.log(Level.SEVERE, e.getMessage());
    }
  }

  @Override
  public Game getActualGame() {
    return game;
  }

  @Override
  public ClientStatus getStatus() {
    return this.status;
  }

  @Override
  public void keyBoardInput(KeyCode input) {
    if (game instanceof MultiplayerGame) {
      var multiplayerGame = (MultiplayerGame) game;
      if (multiplayerGame.getStatus() != PlayerStatus.LOCK_OUT
          && multiplayerGame.getStatus() != PlayerStatus.NOT_STARTED) {
        switch (input) {
          case LEFT, NUMPAD4 -> multiplayerGame.move(Direction.LEFT);
          case RIGHT, NUMPAD6 -> multiplayerGame.move(Direction.RIGHT);
          case DOWN, NUMPAD2 -> multiplayerGame.softDrop();
          case UP, X, NUMPAD3, NUMPAD5 -> multiplayerGame.rotate(true);
          case SHIFT, C, NUMPAD0 -> multiplayerGame.hold();
          case SPACE, NUMPAD8 -> multiplayerGame.hardDrop();
          case CONTROL, NUMPAD1, NUMPAD9 -> multiplayerGame.rotate(false);
        }
      }
    }

  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    switch (evt.getPropertyName()) {
      case "hold" -> sendHoldMino((Mino) evt.getNewValue());
      case "ACTUAL" -> sendTetriminoToOtherPlayer((TetriminoInterface) evt.getNewValue());
      case "ACTION" -> sendStatAction((Action) evt.getNewValue());
      case "PLACE_TETRIMINO" -> lockTetrimino((TetriminoInterface) evt.getNewValue());
      case "STATUS" -> sendStatusUpdate((PlayerStatus) evt.getNewValue());
      case "next" -> {
        if (evt.getNewValue() == null) {
          requestNextMino();
        }
      }
    }
  }
}