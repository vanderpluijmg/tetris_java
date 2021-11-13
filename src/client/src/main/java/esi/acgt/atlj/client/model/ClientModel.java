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

import esi.acgt.atlj.client.connexionServer.Client;
import esi.acgt.atlj.client.connexionServer.ClientInterface;
import esi.acgt.atlj.model.Model;
import esi.acgt.atlj.model.board.Direction;
import esi.acgt.atlj.model.board.ManagedBoard;
import esi.acgt.atlj.model.board.UnmanagedBoard;
import java.beans.PropertyChangeListener;

public class ClientModel extends Model {

  private ManagedBoard player1;
  private UnmanagedBoard player2;
  private ClientInterface client;

  public ClientModel() {
    super();
  }

  public void connect(int port, String host) {
    this.client = new Client(port, host);
  }

  public ClientInterface getClient() {
    return client;
  }

  public void start() {
    this.player1.start();
  }

  public void initManagedBoard(String username) {
    player1 = new ManagedBoard(username);
  }

  @Override
  public void addPropertyChangeListener(PropertyChangeListener[] listener) {
    this.player1.addPropertyChangeListener(listener[0]);
    //this.player2.addPropertyChangeListener(listener[1]);
  }

  @Override
  public void removePropertyChangeListener(PropertyChangeListener listener) {
    this.player1.removePropertyChangeListener(listener);
    //this.player2.removePropertyChangeListener(listener);
  }

  public void move(Direction direction) {
    this.player1.move(direction);
  }

  public void hold() {
    this.player1.hold();
  }

  public void hardDrop() {
    this.player1.hardDrop();
  }

  public void softDrop() {
    this.player1.softDrop();
  }

  public void rotate(boolean clockwise) {
    player1.rotate(clockwise);
  }
}