import java.util.*;

public class aiTicTacToe {

  public int player; // 1 for player 1 and 2 for player 2
  public static boolean firstMove = true;

  public List<positionTicTacToe> deepCopyATicTacToeBoard(List<positionTicTacToe> board) {
    // deep copy of game boards
    List<positionTicTacToe> copiedBoard = new ArrayList<positionTicTacToe>();
    for (int i = 0; i < board.size(); i++) {
      copiedBoard.add(new positionTicTacToe(board.get(i).x, board.get(i).y, board.get(i).z,
          board.get(i).state));
    }
    return copiedBoard;
  }


  private int getStateOfPositionFromBoard(positionTicTacToe position,
      List<positionTicTacToe> board) {
    // a helper function to get state of a certain position in the Tic-Tac-Toe board by given
    // position TicTacToe
    int index = position.x * 16 + position.y * 4 + position.z;
    return board.get(index).state;
  }

  public positionTicTacToe myAIAlgorithm(List<positionTicTacToe> board, int player) {

    positionTicTacToe myNextMove = new positionTicTacToe(0, 0, 0);
    Random rand = new Random();

    // first move is random
    if (firstMove) {
      firstMove = false;
      myNextMove = new positionTicTacToe(rand.nextInt(4), rand.nextInt(4), rand.nextInt(4));
      return myNextMove;
    }
    if (player == 2) {
      Combo combo = alphabeta(board, 1, Integer.MIN_VALUE, Integer.MAX_VALUE, true, player);
      myNextMove = combo.move;
      return myNextMove;
    }

    // get next move
    Combo combo = alphabeta(board, 3, Integer.MIN_VALUE, Integer.MAX_VALUE, true, player);
    myNextMove = combo.move;

    return myNextMove;
  }

  // combo move value pair
  public class Combo {
    positionTicTacToe move;
    int value;

    public Combo(positionTicTacToe m, int v) {
      move = m;
      value = v;
    }
  }

  // alpha beta start function
  public Combo alphabeta(List<positionTicTacToe> b, int depth, int alpha, int beta,
      boolean maximizingPlayer, int player) {

    // variables
    List<positionTicTacToe> board = deepCopyATicTacToeBoard(b);
    List<positionTicTacToe> tempBoard;
    Combo combo;
    Combo maxCombo = new Combo((new positionTicTacToe(0, 0, 0)), Integer.MIN_VALUE);

    // go through each potential moves
    for (int i = 0; i < board.size(); i++) {
      if (board.get(i).state == 0) {

        // play move
        tempBoard = deepCopyATicTacToeBoard(board);
        tempBoard.get(i).state = player;

        // get recursive value
        combo = _alphabeta(tempBoard, board.get(i), depth - 1, alpha, beta, false, player);

        // update maxCombo
        if (combo.value > maxCombo.value
            || (combo.value == maxCombo.value && maxCombo.move.state == -1)) {
          maxCombo = combo;
        }

        // update alpha
        alpha = Math.max(alpha, maxCombo.value);

        // break if there is pruning
        if (alpha >= beta) {
          break;
        }
      }
    }
    return maxCombo;
  }

  // alpha beta helper function that carries move through recursive calls
  public Combo _alphabeta(List<positionTicTacToe> b, positionTicTacToe m, int depth, int alpha,
      int beta, boolean maximizingPlayer, int player) {

    // variables
    List<positionTicTacToe> board = deepCopyATicTacToeBoard(b);
    List<positionTicTacToe> tempBoard;
    Combo combo;
    Combo maxCombo;
    int opponent;
    boolean check = false;

    // set opponent
    if (player == 1) {
      opponent = 2;
    } else {
      opponent = 1;
    }

    // get value and return if depth is 0 or terminal node
    int value = getValue(board);
    if (depth == 0 || value == Integer.MAX_VALUE || value == Integer.MIN_VALUE) {
      return (new Combo(m, value));
    }

    // if maximizing player
    if (maximizingPlayer) {
      maxCombo = new Combo((new positionTicTacToe(0, 0, 0)), Integer.MIN_VALUE);

      // check each move
      for (int i = 0; i < board.size(); i++) {
        if (board.get(i).state == 0) {
          check = true;

          // play move
          tempBoard = deepCopyATicTacToeBoard(board);
          tempBoard.get(i).state = player;

          // get combo through recursion
          combo = _alphabeta(tempBoard, m, depth - 1, alpha, beta, false, player);

          // update maxCombo if necessary
          if (combo.value > maxCombo.value
              || (combo.value == maxCombo.value && maxCombo.move.state == -1)) {
            maxCombo = combo;
          }

          // update alpha
          alpha = Math.max(alpha, maxCombo.value);

          // break if there is pruning
          if (alpha >= beta) {
            break;
          }
        }
      }

      // return correct combo
      if (check) {
        return maxCombo;
      } else {
        return (new Combo(m, value));
      }

      // if minimizing player
    } else {
      maxCombo = new Combo((new positionTicTacToe(0, 0, 0)), Integer.MAX_VALUE);

      // check each move
      for (int i = 0; i < board.size(); i++) {
        if (board.get(i).state == 0) {
          check = true;

          // play move
          tempBoard = deepCopyATicTacToeBoard(board);
          tempBoard.get(i).state = opponent;

          // get combo through recursion
          combo = _alphabeta(tempBoard, m, depth - 1, alpha, beta, true, player);

          // update maxCombo if necessary
          if (combo.value < maxCombo.value
              || (combo.value == maxCombo.value && maxCombo.move.state == -1)) {
            maxCombo = combo;
          }

          // update beta
          beta = Math.min(beta, maxCombo.value);

          // break if there is pruning
          if (alpha >= beta) {
            break;
          }
        }
      }

      // return correct combo
      if (check) {
        return maxCombo;
      } else {
        return (new Combo(m, value));
      }
    }
  }

  // get value of board for specific ai player
  public int getValue(List<positionTicTacToe> board) {

    // variables
    List<List<positionTicTacToe>> boardLines = getBoardLines(board);
    int opponent;
    int value = 0;

    // set opponent
    if (player == 1) {
      opponent = 2;
    } else {
      opponent = 1;
    }

    // go through all board lines
    for (int i = 0; i < boardLines.size(); i++) {

      positionTicTacToe p0 = boardLines.get(i).get(0);
      positionTicTacToe p1 = boardLines.get(i).get(1);
      positionTicTacToe p2 = boardLines.get(i).get(2);
      positionTicTacToe p3 = boardLines.get(i).get(3);

      int state0 = getStateOfPositionFromBoard(p0, board);
      int state1 = getStateOfPositionFromBoard(p1, board);
      int state2 = getStateOfPositionFromBoard(p2, board);
      int state3 = getStateOfPositionFromBoard(p3, board);
      int[] states = {state0, state1, state2, state3};
      int pNumber = 0;
      int oNumber = 0;

      // count number of pieces in each lines
      for (int j = 0; j < 4; j++) {
        if (states[j] == player) {
          pNumber += 1;
        } else if (states[j] == opponent) {
          oNumber += 1;
        }
      }

      // set to max or min value depending on win or loss.
      if (pNumber == 4) {
        return Integer.MAX_VALUE;
      } else if (oNumber == 4) {
        return Integer.MIN_VALUE;
      }

      // add specific value depending on number and order of player pieces in line
      if (pNumber == 1) {
        value += 1;
      } else if (pNumber == 2) {
        if (state0 == player && state1 == player) {
          value += 100;
        } else if (state1 == player && state2 == player) {
          value += 100;
        } else if (state2 == player && state3 == player) {
          value += 100;
        } else {
          value += 1;
        }
      } else if (pNumber == 3) {
        if (state0 == player && state1 == player && state2 == player) {
          value += 1000;
        } else if (state1 == player && state2 == player && state3 == player) {
          value += 1000;
        } else if (state0 == player && state1 == player) {
          value += 100;
        } else if (state2 == player && state3 == player) {
          value += 100;
        } else {
          value += 1;
        }
      }

      // subtract specific value depending on number and order of opponent pieces in line
      if (oNumber == 1) {
        value -= 2;
      } else if (oNumber == 2) {
        if (state0 == opponent && state1 == opponent) {
          value -= 110;
        } else if (state1 == opponent && state2 == opponent) {
          value -= 110;
        } else if (state2 == opponent && state3 == opponent) {
          value -= 110;
        } else {
          value -= 2;
        }
      } else if (oNumber == 3) {
        if (state0 == opponent && state1 == opponent && state2 == opponent) {
          value -= 1100;
        } else if (state1 == opponent && state2 == opponent && state3 == opponent) {
          value -= 1100;
        } else if (state0 == opponent && state1 == opponent) {
          value -= 110;
        } else if (state2 == opponent && state3 == opponent) {
          value -= 110;
        } else {
          value -= 2;
        }
      }
    }
    return value;
  }

  // get all lines on the board
  private List<List<positionTicTacToe>> getBoardLines(List<positionTicTacToe> board) {
    // create a list of winning line so that the game will "brute-force" check if a player satisfied
    // any winning condition(s).
    List<List<positionTicTacToe>> boardLines = new ArrayList<List<positionTicTacToe>>();

    // 48 straight winning lines
    // z axis winning lines
    for (int i = 0; i < 4; i++)
      for (int j = 0; j < 4; j++) {
        List<positionTicTacToe> oneLine = new ArrayList<positionTicTacToe>();
        oneLine.add(board.get(i * 16 + j * 4 + 0));
        oneLine.add(board.get(i * 16 + j * 4 + 1));
        oneLine.add(board.get(i * 16 + j * 4 + 2));
        oneLine.add(board.get(i * 16 + j * 4 + 3));
        boardLines.add(oneLine);
      }
    // y axis winning lines
    for (int i = 0; i < 4; i++)
      for (int j = 0; j < 4; j++) {
        List<positionTicTacToe> oneLine = new ArrayList<positionTicTacToe>();
        oneLine.add(board.get(i * 16 + 0 * 4 + j));
        oneLine.add(board.get(i * 16 + 1 * 4 + j));
        oneLine.add(board.get(i * 16 + 2 * 4 + j));
        oneLine.add(board.get(i * 16 + 3 * 4 + j));
        boardLines.add(oneLine);
      }
    // x axis winning lines
    for (int i = 0; i < 4; i++)
      for (int j = 0; j < 4; j++) {
        List<positionTicTacToe> oneLine = new ArrayList<positionTicTacToe>();
        oneLine.add(board.get(0 * 16 + i * 4 + j));
        oneLine.add(board.get(1 * 16 + i * 4 + j));
        oneLine.add(board.get(2 * 16 + i * 4 + j));
        oneLine.add(board.get(3 * 16 + i * 4 + j));
        boardLines.add(oneLine);
      }

    // 12 main diagonal winning lines
    // xz plane-4
    for (int i = 0; i < 4; i++) {
      List<positionTicTacToe> oneLine = new ArrayList<positionTicTacToe>();
      oneLine.add(board.get(0 * 16 + i * 4 + 0));
      oneLine.add(board.get(1 * 16 + i * 4 + 1));
      oneLine.add(board.get(2 * 16 + i * 4 + 2));
      oneLine.add(board.get(3 * 16 + i * 4 + 3));
      boardLines.add(oneLine);
    }
    // yz plane-4
    for (int i = 0; i < 4; i++) {
      List<positionTicTacToe> oneLine = new ArrayList<positionTicTacToe>();
      oneLine.add(board.get(i * 16 + 0 * 4 + 0));
      oneLine.add(board.get(i * 16 + 1 * 4 + 1));
      oneLine.add(board.get(i * 16 + 2 * 4 + 2));
      oneLine.add(board.get(i * 16 + 3 * 4 + 3));
      boardLines.add(oneLine);
    }
    // xy plane-4
    for (int i = 0; i < 4; i++) {
      List<positionTicTacToe> oneLine = new ArrayList<positionTicTacToe>();
      oneLine.add(board.get(0 * 16 + 0 * 4 + i));
      oneLine.add(board.get(1 * 16 + 1 * 4 + i));
      oneLine.add(board.get(2 * 16 + 2 * 4 + i));
      oneLine.add(board.get(3 * 16 + 3 * 4 + i));
      boardLines.add(oneLine);
    }

    // 12 anti diagonal winning lines
    // xz plane-4
    for (int i = 0; i < 4; i++) {
      List<positionTicTacToe> oneLine = new ArrayList<positionTicTacToe>();
      oneLine.add(board.get(0 * 16 + i * 4 + 3));
      oneLine.add(board.get(1 * 16 + i * 4 + 2));
      oneLine.add(board.get(2 * 16 + i * 4 + 1));
      oneLine.add(board.get(3 * 16 + i * 4 + 0));
      boardLines.add(oneLine);
    }
    // yz plane-4
    for (int i = 0; i < 4; i++) {
      List<positionTicTacToe> oneLine = new ArrayList<positionTicTacToe>();
      oneLine.add(board.get(i * 16 + 0 * 4 + 3));
      oneLine.add(board.get(i * 16 + 1 * 4 + 2));
      oneLine.add(board.get(i * 16 + 2 * 4 + 1));
      oneLine.add(board.get(i * 16 + 3 * 4 + 0));
      boardLines.add(oneLine);
    }
    // xy plane-4
    for (int i = 0; i < 4; i++) {
      List<positionTicTacToe> oneLine = new ArrayList<positionTicTacToe>();
      oneLine.add(board.get(0 * 16 + 3 * 4 + i));
      oneLine.add(board.get(1 * 16 + 2 * 4 + i));
      oneLine.add(board.get(2 * 16 + 1 * 4 + i));
      oneLine.add(board.get(3 * 16 + 0 * 4 + i));
      boardLines.add(oneLine);
    }

    // 4 additional diagonal winning lines
    List<positionTicTacToe> oneLine = new ArrayList<positionTicTacToe>();
    oneLine.add(board.get(0 * 16 + 0 * 4 + 0));
    oneLine.add(board.get(1 * 16 + 1 * 4 + 1));
    oneLine.add(board.get(2 * 16 + 2 * 4 + 2));
    oneLine.add(board.get(3 * 16 + 3 * 4 + 3));
    boardLines.add(oneLine);

    oneLine = new ArrayList<positionTicTacToe>();
    oneLine.add(board.get(0 * 16 + 0 * 4 + 3));
    oneLine.add(board.get(1 * 16 + 1 * 4 + 2));
    oneLine.add(board.get(2 * 16 + 2 * 4 + 1));
    oneLine.add(board.get(3 * 16 + 3 * 4 + 0));
    boardLines.add(oneLine);

    oneLine = new ArrayList<positionTicTacToe>();
    oneLine.add(board.get(3 * 16 + 0 * 4 + 0));
    oneLine.add(board.get(2 * 16 + 1 * 4 + 1));
    oneLine.add(board.get(1 * 16 + 2 * 4 + 2));
    oneLine.add(board.get(0 * 16 + 3 * 4 + 3));
    boardLines.add(oneLine);

    oneLine = new ArrayList<positionTicTacToe>();
    oneLine.add(board.get(0 * 16 + 3 * 4 + 0));
    oneLine.add(board.get(1 * 16 + 2 * 4 + 1));
    oneLine.add(board.get(2 * 16 + 1 * 4 + 2));
    oneLine.add(board.get(3 * 16 + 0 * 4 + 3));
    boardLines.add(oneLine);

    return boardLines;

  }

  private List<List<positionTicTacToe>> initializeWinningLines() {
    // create a list of winning line so that the game will "brute-force" check if a player satisfied
    // any winning condition(s).
    List<List<positionTicTacToe>> winningLines = new ArrayList<List<positionTicTacToe>>();

    // 48 straight winning lines
    // z axis winning lines
    for (int i = 0; i < 4; i++)
      for (int j = 0; j < 4; j++) {
        List<positionTicTacToe> oneWinCondtion = new ArrayList<positionTicTacToe>();
        oneWinCondtion.add(new positionTicTacToe(i, j, 0, -1));
        oneWinCondtion.add(new positionTicTacToe(i, j, 1, -1));
        oneWinCondtion.add(new positionTicTacToe(i, j, 2, -1));
        oneWinCondtion.add(new positionTicTacToe(i, j, 3, -1));
        winningLines.add(oneWinCondtion);
      }
    // y axis winning lines
    for (int i = 0; i < 4; i++)
      for (int j = 0; j < 4; j++) {
        List<positionTicTacToe> oneWinCondtion = new ArrayList<positionTicTacToe>();
        oneWinCondtion.add(new positionTicTacToe(i, 0, j, -1));
        oneWinCondtion.add(new positionTicTacToe(i, 1, j, -1));
        oneWinCondtion.add(new positionTicTacToe(i, 2, j, -1));
        oneWinCondtion.add(new positionTicTacToe(i, 3, j, -1));
        winningLines.add(oneWinCondtion);
      }
    // x axis winning lines
    for (int i = 0; i < 4; i++)
      for (int j = 0; j < 4; j++) {
        List<positionTicTacToe> oneWinCondtion = new ArrayList<positionTicTacToe>();
        oneWinCondtion.add(new positionTicTacToe(0, i, j, -1));
        oneWinCondtion.add(new positionTicTacToe(1, i, j, -1));
        oneWinCondtion.add(new positionTicTacToe(2, i, j, -1));
        oneWinCondtion.add(new positionTicTacToe(3, i, j, -1));
        winningLines.add(oneWinCondtion);
      }

    // 12 main diagonal winning lines
    // xz plane-4
    for (int i = 0; i < 4; i++) {
      List<positionTicTacToe> oneWinCondtion = new ArrayList<positionTicTacToe>();
      oneWinCondtion.add(new positionTicTacToe(0, i, 0, -1));
      oneWinCondtion.add(new positionTicTacToe(1, i, 1, -1));
      oneWinCondtion.add(new positionTicTacToe(2, i, 2, -1));
      oneWinCondtion.add(new positionTicTacToe(3, i, 3, -1));
      winningLines.add(oneWinCondtion);
    }
    // yz plane-4
    for (int i = 0; i < 4; i++) {
      List<positionTicTacToe> oneWinCondtion = new ArrayList<positionTicTacToe>();
      oneWinCondtion.add(new positionTicTacToe(i, 0, 0, -1));
      oneWinCondtion.add(new positionTicTacToe(i, 1, 1, -1));
      oneWinCondtion.add(new positionTicTacToe(i, 2, 2, -1));
      oneWinCondtion.add(new positionTicTacToe(i, 3, 3, -1));
      winningLines.add(oneWinCondtion);
    }
    // xy plane-4
    for (int i = 0; i < 4; i++) {
      List<positionTicTacToe> oneWinCondtion = new ArrayList<positionTicTacToe>();
      oneWinCondtion.add(new positionTicTacToe(0, 0, i, -1));
      oneWinCondtion.add(new positionTicTacToe(1, 1, i, -1));
      oneWinCondtion.add(new positionTicTacToe(2, 2, i, -1));
      oneWinCondtion.add(new positionTicTacToe(3, 3, i, -1));
      winningLines.add(oneWinCondtion);
    }

    // 12 anti diagonal winning lines
    // xz plane-4
    for (int i = 0; i < 4; i++) {
      List<positionTicTacToe> oneWinCondtion = new ArrayList<positionTicTacToe>();
      oneWinCondtion.add(new positionTicTacToe(0, i, 3, -1));
      oneWinCondtion.add(new positionTicTacToe(1, i, 2, -1));
      oneWinCondtion.add(new positionTicTacToe(2, i, 1, -1));
      oneWinCondtion.add(new positionTicTacToe(3, i, 0, -1));
      winningLines.add(oneWinCondtion);
    }
    // yz plane-4
    for (int i = 0; i < 4; i++) {
      List<positionTicTacToe> oneWinCondtion = new ArrayList<positionTicTacToe>();
      oneWinCondtion.add(new positionTicTacToe(i, 0, 3, -1));
      oneWinCondtion.add(new positionTicTacToe(i, 1, 2, -1));
      oneWinCondtion.add(new positionTicTacToe(i, 2, 1, -1));
      oneWinCondtion.add(new positionTicTacToe(i, 3, 0, -1));
      winningLines.add(oneWinCondtion);
    }
    // xy plane-4
    for (int i = 0; i < 4; i++) {
      List<positionTicTacToe> oneWinCondtion = new ArrayList<positionTicTacToe>();
      oneWinCondtion.add(new positionTicTacToe(0, 3, i, -1));
      oneWinCondtion.add(new positionTicTacToe(1, 2, i, -1));
      oneWinCondtion.add(new positionTicTacToe(2, 1, i, -1));
      oneWinCondtion.add(new positionTicTacToe(3, 0, i, -1));
      winningLines.add(oneWinCondtion);
    }

    // 4 additional diagonal winning lines
    List<positionTicTacToe> oneWinCondtion = new ArrayList<positionTicTacToe>();
    oneWinCondtion.add(new positionTicTacToe(0, 0, 0, -1));
    oneWinCondtion.add(new positionTicTacToe(1, 1, 1, -1));
    oneWinCondtion.add(new positionTicTacToe(2, 2, 2, -1));
    oneWinCondtion.add(new positionTicTacToe(3, 3, 3, -1));
    winningLines.add(oneWinCondtion);

    oneWinCondtion = new ArrayList<positionTicTacToe>();
    oneWinCondtion.add(new positionTicTacToe(0, 0, 3, -1));
    oneWinCondtion.add(new positionTicTacToe(1, 1, 2, -1));
    oneWinCondtion.add(new positionTicTacToe(2, 2, 1, -1));
    oneWinCondtion.add(new positionTicTacToe(3, 3, 0, -1));
    winningLines.add(oneWinCondtion);

    oneWinCondtion = new ArrayList<positionTicTacToe>();
    oneWinCondtion.add(new positionTicTacToe(3, 0, 0, -1));
    oneWinCondtion.add(new positionTicTacToe(2, 1, 1, -1));
    oneWinCondtion.add(new positionTicTacToe(1, 2, 2, -1));
    oneWinCondtion.add(new positionTicTacToe(0, 3, 3, -1));
    winningLines.add(oneWinCondtion);

    oneWinCondtion = new ArrayList<positionTicTacToe>();
    oneWinCondtion.add(new positionTicTacToe(0, 3, 0, -1));
    oneWinCondtion.add(new positionTicTacToe(1, 2, 1, -1));
    oneWinCondtion.add(new positionTicTacToe(2, 1, 2, -1));
    oneWinCondtion.add(new positionTicTacToe(3, 0, 3, -1));
    winningLines.add(oneWinCondtion);

    return winningLines;

  }

  public aiTicTacToe(int setPlayer) {
    player = setPlayer;
  }
}
