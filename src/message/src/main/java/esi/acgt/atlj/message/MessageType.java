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

package esi.acgt.atlj.message;

/**
 * All different type of message that are possible to send/receive from client/server
 */
public enum MessageType {
  ASK_PIECE, // asks a piece to the server
  ADD_TETRIMINO, //Adds a tetrimino to the unmanaged board of the other player
  SEND_PIECE, //Send a piece from the server to the client
  SEND_SCORE, //Sends the score to the server or client
  REMOVE_LINE, //Removes a line to the unmanaged board of the other player
  PLAYER_STATUS, //Send the status to the player
  UPDATE_PIECE_UNMANAGED, //Updates piece of unmanaged board of other player
  SEND_NAME,// Sends username of player to other player.
  NUMBER_LINES, // Send the number of line the player has destructed
  HOLD, // Sends the mino the player is holding
  ACTION, //Action player would like to do
  SEND_BOARD // Sends board to spectator
}
