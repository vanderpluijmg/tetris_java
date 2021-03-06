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

package esi.acgt.atlj.message.messageTypes;

import esi.acgt.atlj.message.Message;
import esi.acgt.atlj.message.MessageType;

import esi.acgt.atlj.model.tetrimino.TetriminoInterface;

/**
 * Client tell the server to add to its unmanaged board a tetrimino
 */
public class AddTetrimino extends Message {

  /**
   * Tetrimino to send
   */
  private final TetriminoInterface tetrimino;

  /**
   * Constructor for tetrimino message.
   *
   * @param tetrimino Tetrimino to add.
   */
  public AddTetrimino(TetriminoInterface tetrimino) {
    this.tetrimino = tetrimino;
    this.messageType = MessageType.ADD_TETRIMINO;
  }

  /**
   * Constructor for add tetrimino type of message.
   */
  public TetriminoInterface getTetrimino() {
    return tetrimino;
  }
}
