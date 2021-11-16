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

package esi.acgt.atlj.server;


import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AbstractServer implements Runnable {

  /**
   * The server socket: listens for clients who want to connect.
   */
  private ServerSocket serverSocket = null;

  /**
   * The connection listener thread.
   */
  private Thread connectionListener = null;

  /**
   * The port number
   */
  private int port;

  /**
   * The server timeout while for accepting connections. After timing out, the server will check to
   * see if a command to stop the server has been issued; it not it will resume accepting
   * connections. Set to half a second by default.
   */
  private int timeout = 500;

  /**
   * The maximum queue length; i.e. the maximum number of clients that can be waiting to connect.
   * Set to 10 by default.
   */
  private int backlog = 10;

  /**
   * The thread group associated with client threads. Each member of the thread group is a <code>
   * ConnectionToClient </code>.
   */
  private final List<Thread> threads;

  /**
   * Indicates if the listening thread is ready to stop. Set to true by default.
   */
  private boolean readyToStop = true;

  /**
   * Constructs a new server.
   *
   * @param port the port number on which to listen.
   */
  public AbstractServer(int port) {
    this.port = port;
    threads = new ArrayList<>();
  }

  /**
   * Closes the server socket and the connections with all clients. Any exception thrown while
   * closing a client is ignored. If one wishes to catch these exceptions, then clients should be
   * individually closed before calling this method. The method also stops listening if this thread
   * is running. If the server is already closed, this call has no effect.
   *
   * @throws IOException if an I/O error occurs while closing the server socket.
   */
  final public void close() throws IOException {
    if (serverSocket == null) {
      return;
    }
    stopListening();
    try {
      serverSocket.close();
    } finally {
      synchronized (this) {
        for (Thread clientThreadList1 : threads) {
          try {
            ((CustomClientThread) clientThreadList1).close();
          } catch (Exception ex) {
          }
        }
        serverSocket = null;
      }
      try {
        connectionListener.join();
      } catch (InterruptedException | NullPointerException ex) {
      }
      serverClosed();
    }
  }

  /**
   * Returns the port number.
   *
   * @return the port number.
   */
  final public int getPort() {
    return port;
  }

  /**
   * Returns true if the server is listening and therefore ready to accept new clients.
   *
   * @return true if the server is listening.
   */
  final public boolean isListening() {
    return connectionListener != null && connectionListener.isAlive(); // modified in version 2.31
  }

  /**
   * Begins the thread that waits for new clients. If the server is already in listening mode, this
   * call has no effect.
   *
   * @throws IOException if an I/O error occurs when creating the server socket.
   */
  final public void listen() throws IOException {
    if (!isListening()) {
      if (serverSocket == null) {
        serverSocket = new ServerSocket(getPort(), backlog);
      }
      serverSocket.setSoTimeout(timeout);
      connectionListener = new Thread(this);
      connectionListener.start();
    }
  }

  /**
   * Causes the server to stop accepting new connections.
   */
  final public void stopListening() {
    readyToStop = true;
  }

  /**
   * Hook method called each time a new client connection is accepted. The default implementation
   * does nothing. This method does not have to be synchronized since only one client can be
   * accepted at a time.
   *
   * @param client the connection connected to the client.
   */
  protected void clientConnected(CustomClientThread client) {
  }

  /**
   * Hook method called each time a client disconnects. The client is garantee to be disconnected
   * but the thread is still active until it is asynchronously removed from the thread group. The
   * default implementation does nothing. The method may be overridden by subclasses but should
   * remains synchronized.
   *
   * @param client the connection with the client.
   */
  synchronized protected void clientDisconnected(
      CustomClientThread client) {
  }

  /**
   * Hook method called each time an exception is thrown in a ConnectionToClient thread. The method
   * may be overridden by subclasses but should remains synchronized. Most exceptions will cause the
   * end of the client's thread except for <code>ClassNotFoundException</code>s received when an
   * object of unknown class is received and for the
   * <code>RuntimeException</code>s that can be thrown by the message handling
   * method implemented by the user.
   *
   * @param client    the client that raised the exception.
   * @param exception the exception thrown.
   */
  synchronized protected void clientException(
      CustomClientThread client, Throwable exception) {
  }

  /**
   * Hook method called when the server stops accepting connections because an exception has been
   * raised. The default implementation does nothing. This method may be overriden by subclasses.
   *
   * @param exception the exception raised.
   */
  protected void listeningException(Throwable exception) {
  }

  /**
   * Hook method called when the server starts listening for connections. The default implementation
   * does nothing. The method may be overridden by subclasses.
   */
  protected void serverStarted() {
  }

  /**
   * Hook method called when the server stops accepting connections. The default implementation does
   * nothing. This method may be overriden by subclasses.
   */
  protected void serverStopped() {
  }

  /**
   * Hook method called when the server is clased. The default implementation does nothing. This
   * method may be overriden by subclasses. When the server is closed while still listening,
   * serverStopped() will also be called.
   */
  protected void serverClosed() {
  }

  /**
   * Handles a command sent from one client to the server. This MUST be implemented by subclasses,
   * who should respond to messages. This method is called by a synchronized method so it is also
   * implcitly synchronized.
   *
   * @param msg    the message sent.
   * @param client the connection connected to the client that sent the message.
   */
  protected abstract void handleMessageFromClient(
      Object msg, CustomClientThread client);


  /**
   * Receives a command sent from the client to the server. Called by the run method of
   * <code>ConnectionToClient</code> instances that are watching for messages coming from the
   * server This method is synchronized to ensure that whatever effects it has do not conflict with
   * work being done by other threads. The method simply calls the
   * <code>handleMessageFromClient</code> slot method.
   *
   * @param msg    the message sent.
   * @param client the connection connected to the client that sent the message.
   */
  final synchronized void receiveMessageFromClient(
      Object msg, CustomClientThread client) {
    this.handleMessageFromClient(msg, client);
  }

  /**
   * Runs the listening thread that allows clients to connect. Not to be called.
   */
  @Override
  final public void run() {
    readyToStop = false;
    serverStarted();

    try {
      // Repeatedly waits for a new client connection, accepts it, and
      // starts a new thread to handle data exchange.
      while (!readyToStop) {
        try {
          // Wait here for new connection attempts, or a timeout
          Socket clientSocket = serverSocket.accept();
          // When a client is accepted, create a thread to handle
          // the data exchange, then add it to thread group
          synchronized (this) {
            if (!readyToStop) {
              CustomClientThread client = new CustomClientThread(
                  clientSocket, this, threads.size());
              this.threads.add(client);
            }
          }
        } catch (InterruptedIOException exception) {
          // This will be thrown when a timeout occurs.
          // The server will continue to listen if not ready to stop.
        }
      }
    } catch (IOException exception) {
      if (!readyToStop) {
        // Closing the socket must have thrown a SocketException
        listeningException(exception);
      }
    } finally {
      readyToStop = true;
      connectionListener = null;
      // call the hook method to notify that the server has stopped
      serverStopped();
    }
  }

}
