# 實驗報告：Reversi 和 Peace 遊戲系統

## 項目說明

本項目是一個基於 Java 的多遊戲系統，實現了兩種棋盤遊戲：**Reversi（黑白棋）** 和 **Peace（簡化版棋盤遊戲）**。系統支持多個遊戲同時進行，並提供以下功能：

1. **玩家交互**：支持玩家輸入名稱、選擇遊戲並進行操作。
2. **遊戲邏輯處理**：
   - Reversi：實現棋子合法性檢查、翻轉邏輯和勝負判斷。
   - Peace：簡化的棋盤遊戲，允許玩家在空位置放置棋子。
3. **棋盤顯示**：動態更新棋盤狀態，顯示玩家分數和當前遊戲狀態。
4. **多遊戲管理**：支持同時管理多個遊戲，玩家可在不同遊戲間切換。

該系統採用了模塊化設計，代碼結構清晰，便於擴展和維護。


## 源代碼文件及其主要功能

| **源代碼文件**         | **功能**                                                         | **主要邏輯**                                                         |
|-------------------|--------------------------------------------------------------|------------------------------------------------------------------|
| App.java          | 程序的入口點，負責啟動遊戲。                                   | 創建 Scanner 對象，用於讀取用戶輸入，調用 GameManager.start(scanner) 開始遊戲。 |
| GameManager.java  | 管理多個遊戲的邏輯，包括遊戲初始化、切換遊戲、處理玩家輸入等。 | 初始化玩家信息；創建並管理多個遊戲（Peace 和 Reversi）；處理玩家輸入（如移動、切換遊戲、退出等）；調用 GameView 顯示遊戲狀態。 |
| GameEngine.java   | 定義遊戲引擎的接口，為所有遊戲提供統一的操作方法。           | 定義遊戲基本操作，如放置棋子、檢查遊戲是否結束、獲取棋盤狀態等。            |
| Reversi.java      | 實現 Reversi 遊戲的邏輯。                                     | 使用方向數組檢查棋子合法放置位置；翻轉被夾住的棋子；計算棋盤上各棋子的數量。    |
| Peace.java        | 實現 Peace 遊戲的邏輯，簡化版棋盤遊戲。                       | 允許玩家在空位置放置棋子；檢查棋盤是否已滿。                          |
| GameSetup.java    | 負責初始化玩家信息。                                         | 提示用戶輸入玩家名稱；分配棋子類型（黑棋或白棋）。                      |
| GameView.java     | 負責遊戲視覺化顯示，包括棋盤打印、玩家信息顯示等。             | 打印棋盤狀態；顯示當前玩家、分數和遊戲狀態；提供清屏功能。                |
| InputUtils.java   | 處理用戶輸入，解析輸入格式並進行驗證。                       | 支持多種輸入格式（如棋盤坐標、切換遊戲、退出等）；驗證輸入是否合法。         |
| Board.java        | 表示棋盤的數據結構，提供棋盤操作方法。                         | 初始化棋盤；設置和獲取棋盤上的棋子；檢查棋盤是否已滿。                    |
| Piece.java        | 定義棋子的類型（如黑棋、白棋、空位等）。                     | 使用枚舉類型表示不同棋子；提供棋子的符號表示。                          |
| Player.java       | 表示玩家信息。                                               | 存儲玩家名稱和棋子類型；提供玩家信息的格式化輸出。                        |



## 关键代码及其设计思路


### 1. **Reversi 的合法性檢查**
``` java:src/Reversi.java
public boolean canPlacePiece(Piece piece) {
       // 清除棋盤上的 CANPLACE 標記
       for (int i = 0; i < Board.SIZE; i++) {
           for (int j = 0; j < Board.SIZE; j++) {
               if (board.getWhatPiece(i, j) == Piece.CANPLACE) {
                   board.setPiece(i, j, Piece.EMPTY);
               }
           }
       }

       boolean canPlace = false;
       for (int i = 0; i < Board.SIZE; i++) {
           for (int j = 0; j < Board.SIZE; j++) {
               // 檢查當前位置是否為空
               if (board.getWhatPiece(i, j) != Piece.EMPTY) {
                   continue;
               }

               boolean isValidMove = false;

               // 遍歷所有方向
               for (int k = 0; k < directions.length; k++) {
                   int x = directions[k][0];
                   int y = directions[k][1];
                   int steps = 0;

                   // 沿著方向檢查
                   while (i + x >= 0 && i + x < Board.SIZE && j + y >= 0 && j + y < Board.SIZE) {
                       if (board.getWhatPiece(i + x, j + y) == Piece.EMPTY ||
                               board.getWhatPiece(i + x, j + y) == Piece.CANPLACE) {
                           break;
                       }

                       if (board.getWhatPiece(i + x, j + y) == piece) {
                           if (steps > 0) { // 確保至少有一個棋子被夾住
                               isValidMove = true;
                           }
                           break;
                       }

                       steps++;
                       x += directions[k][0];
                       y += directions[k][1];
                   }
               }

               // 如果該位置合法，標記為 CANPLACE
               if (isValidMove) {
                   board.setPiece(i, j, Piece.CANPLACE);
                   canPlace = true;
               }
           }
       }

       return canPlace;
   }
```  

#### 設計思路
- 使用方向數組（`directions`）遍歷每個可能的方向。
- 檢查是否存在至少一個夾住對方棋子的情況。
- 如果合法，將該位置標記為 `CANPLACE`。


### 2. **Reversi 的棋子翻轉邏輯**

``` java:src/Reversi.java
public void placePiece(int col, int row, Piece piece) {
    passCounter = 0;
    // 確保當前位置為合法位置
    if (board.getWhatPiece(col, row) == Piece.CANPLACE) {
        board.setPiece(col, row, piece);

        for (int k = 0; k < directions.length; k++) {
            int dx = directions[k][0];
            int dy = directions[k][1];
            int x = dx;
            int y = dy;
            // 用於存儲需要翻轉的棋子位置
            java.util.ArrayList<int[]> toFlip = new java.util.ArrayList<>();
            boolean validDirection = false;

            while (col + x >= 0 && col + x < Board.SIZE && row + y >= 0 && row + y < Board.SIZE) {
                Piece currentPiece = board.getWhatPiece(col + x, row + y);

                if (currentPiece == Piece.EMPTY || currentPiece == Piece.CANPLACE) {
                    break;
                } else if (currentPiece == piece) {
                    validDirection = true;
                    break;
                } else {
                    toFlip.add(new int[] { col + x, row + y });
                }

                x += dx;
                y += dy;
            }

            // 如果方向合法，翻轉所有記錄的棋子
            if (validDirection) {
                for (int[] pos : toFlip) {
                    board.setPiece(pos[0], pos[1], piece);
                }
            }
        }
    }
}
```
#### 設計思路
- 遍歷所有方向，記錄需要翻轉的棋子位置。
- 當找到合法方向時，翻轉所有記錄的棋子。


### 3. **InputUtils 的輸入解析與驗證**

```java:src/InputUtils.java
public static int[] parseInput(String input) throws IllegalArgumentException {
    // 移除不可打印字符並修剪輸入
    input = input.replaceAll("[^\\p{Print}]", "").trim();

    if (input.equalsIgnoreCase("quit")) {
        return new int[] { 0, -2 }; // 用戶選擇退出遊戲
    }

    if (input.equalsIgnoreCase("pass")) {
        return new int[] { 0, -3 }; // 用戶選擇跳過回合
    }

    if (input.equalsIgnoreCase("peace")) {
        return new int[] { 0, -4 }; // 用戶選擇開始 Peace 遊戲
    }

    if (input.equalsIgnoreCase("reversi")) {
        return new int[] { 0, -5 }; // 用戶選擇開始 Reversi 遊戲
    }

    // 檢查是否為單個數字，表示棋盤索引
    if (input.matches("-?\\d+")) {
        int boardIndex = Integer.parseInt(input) - 1;
        if (boardIndex < 0 || boardIndex >= GameManager.getNumberOfGames()) {
            throw new IllegalArgumentException("Invalid board index");
        }
        return new int[] { boardIndex, -1 }; // 返回棋盤索引
    }

    // 檢查是否為合法的棋盤坐標
    Matcher matcher = INPUT_PATTERN.matcher(input);
    if (!matcher.find()) {
        throw new IllegalArgumentException("Invalid input format");
    }

    int row = Integer.parseInt(matcher.group(1)) - 1;
    int col = Character.toLowerCase(matcher.group(2).charAt(0)) - 'a';

    return new int[] { row, col }; // 返回行和列
}
```

#### 設計思路

- **輸入格式處理：**
移除不可打印字符，確保輸入的清潔性。
支持多種輸入命令（如 quit、pass、peace、reversi 等）。

- **棋盤索引檢查：**
如果輸入為數字，則將其解析為棋盤索引，並檢查是否在有效範圍內。

- **棋盤坐標解析：**
使用正則表達式匹配合法的棋盤坐標（如 1A 或 8H）。
將坐標轉換為數組形式，方便後續處理。



### 4. InputUtils 的輸入讀取與驗證
```java:src/InputUtils.java
public static int[] readValidInput(Scanner scanner, GameEngine engine, Piece piece) {
    while (true) {
        String input = scanner.nextLine().trim();
        if (input.isEmpty())
            continue; // 忽略空行
        try {
            int[] move = parseInput(input);
            if (move[1] <= -1) {
                return move; // 返回特殊命令（如退出、跳過回合等）
            }
            // 檢查是否可以放置棋子
            engine.canPlacePiece(piece);

            if (engine.getClass().getSimpleName().equals("Reversi")) {
                if (engine.getBoard().getWhatPiece(move[0], move[1]) == Piece.CANPLACE) {
                    return move; // 合法位置
                } else {
                    System.out.println("This position cannot be placed. Please try again.");
                    continue;
                }
            }
            if (engine.getClass().getSimpleName().equals("Peace")) {
                if (engine.getBoard().getWhatPiece(move[0], move[1]) == Piece.EMPTY) {
                    return move; // 合法位置
                } else {
                    System.out.println("This position cannot be placed. Please try again.");
                    continue;
                }
            }
        } catch (IllegalArgumentException e) {
            System.out.printf(
                    "Invalid input format. Please enter a number (1-8) followed by a letter (A-H) / a board number (1-%d) / new game (peace / reversi) / quit the game (quit): ",
                    GameManager.getNumberOfGames());
        }
    }
}
```

#### 設計思路

- **循環讀取輸入：**
使用 Scanner 循環讀取用戶輸入，忽略空行。

- **解析輸入：**
調用 parseInput 方法解析輸入，處理特殊命令（如退出、跳過回合等）。

- **合法性檢查:**
根據當前遊戲類型（Reversi 或 Peace），檢查輸入位置是否可以放置棋子。
如果位置不合法，提示用戶重新輸入。

- **錯誤處理：**
捕獲非法輸入的異常，提示用戶正確的輸入格式。



### 5. **GameView 的棋盤顯示與遊戲狀態管理**

```java:src/GameView.java
public static void printBoard(GameEngine engine, Board board, Player currentPlayer, Player blackPlayer,
        Player whitePlayer) {
    clearConsole();

    String blackplayerStr = null;
    String whiteplayerStr = null;
    String blackPlayerScore = null;
    String whitePlayerScore = null;

    if (engine instanceof Reversi) {
        whitePlayerScore = Integer.toString(GameManager.getCurrentGame().getHowManyPieces(whitePlayer.pieceType));
        blackPlayerScore = Integer.toString(GameManager.getCurrentGame().getHowManyPieces(blackPlayer.pieceType));
    }

    whiteplayerStr = String.format("Player [%s] ", whitePlayer.getName());
    blackplayerStr = String.format("Player [%s] ", blackPlayer.getName());

    int nameLengthDiff = Math.max(blackplayerStr.length(), whiteplayerStr.length())
            - Math.min(blackplayerStr.length(),
                    whiteplayerStr.length());
    if (nameLengthDiff > 0) {
        if (blackplayerStr.length() > whiteplayerStr.length()) {
            whiteplayerStr = String.format("%s%s", whiteplayerStr, " ".repeat(nameLengthDiff));
        } else {
            blackplayerStr = String.format("%s%s", blackplayerStr, " ".repeat(nameLengthDiff));
        }
    }

    if (currentPlayer == whitePlayer) {
        whiteplayerStr = String.format("%s" + whitePlayer.getpieceType(), whiteplayerStr);
    } else if (currentPlayer == blackPlayer) {
        blackplayerStr = String.format("%s" + blackPlayer.getpieceType(), blackplayerStr);
    }

    System.out.println("  A B C D E F G H");
    for (int i = 0; i < Board.SIZE; i++) {
        String left = buildLeftSection(engine, board, i);
        String middle = buildMiddleSection(engine, blackPlayerScore, whitePlayerScore, blackplayerStr,
                whiteplayerStr, i);
        String right = buildRightSection(i);

        System.out.printf("%-25s %-30s %-50s%n", left, middle, right);
    }

    // 判斷遊戲是否結束
    boolean allGamesOver = true;
    for (GameEngine game : GameManager.games) {
        if (!game.isGameOver()) {
            allGamesOver = false;
            break;
        }
    }

    if (engine instanceof Reversi) {
        if (engine.getBoard().isBoardFull() || (engine.isGameOver() && engine.getPassCounter() == 2)) {
            System.out.println("Game " + (engine.getGameID() + 1) + " is over now.");
            if (GameManager.getCurrentGame() instanceof Reversi) {
                engine = GameManager.getCurrentGame();
                if (engine.getHowManyPieces(Piece.BLACK) > engine.getHowManyPieces(Piece.WHITE)) {
                    System.out.println("Player [" + blackPlayer.getName() + "]  wins!");
                } else if (engine.getHowManyPieces(Piece.BLACK) < engine.getHowManyPieces(Piece.WHITE)) {
                    System.out.println("Player [" + whitePlayer.getName() + "]  wins!");
                } else {
                    System.out.println("It's a tie!");
                }
            }
            if (allGamesOver) {
                System.out.println("All Games over! All the boards are full.");
                System.out.print("Please enter a new game (peace / reversi) / quit the game (quit) : ");
            } else {
                System.out.println(
                        "Please enter another board number to continue / new game (peace / reversi) / quit the game (quit) : ");
            }
        } else {
            if (!engine.canPlacePiece(currentPlayer.pieceType) && engine.getPassCounter() < 2) {
                System.out.printf("Player " + currentPlayer.getName() +
                        ", you do not have place for your piece, please enter pass / game number (1-%d) / new game (peace / reversi) / quit : ",
                        GameManager.getNumberOfGames());
                return;
            } else if (engine.getPassCounter() < 2) {
                System.out.printf("Player " + currentPlayer.getName() +
                        ", please enter your move (1-8,a-h) / game number (1-%d) / new game (peace / reversi) / quit the game (quit) : ",
                        GameManager.getNumberOfGames());
            }
        }
    }
}
```

#### 設計思路

- **棋盤顯示：**
使用 buildLeftSection 方法構建棋盤的左側部分，顯示棋盤的行和列以及棋子狀態。
使用 buildMiddleSection 方法顯示當前遊戲的分數和玩家信息。
使用 buildRightSection 方法顯示遊戲列表，方便玩家切換遊戲。

- **遊戲狀態管理：**

判斷當前遊戲是否結束，並根據遊戲類型（Reversi 或 Peace）顯示勝負結果或平局信息。
如果所有遊戲結束，提示玩家開始新遊戲或退出。

- **玩家提示：**
根據當前玩家的狀態，提示合法的輸入選項（如移動、跳過回合、切換遊戲等）。
如果當前玩家無法進行操作，提示其跳過回合。

- **清屏功能：**
使用 clearConsole 方法清除控制台，保持界面整潔。
根據操作系統選擇適合的清屏命令（Windows 使用 cls，macOS/Linux 使用 ANSI 控制碼）。



### 6. **GameManager 的遊戲管理邏輯**

```java:src/GameManager.java
public static void start(Scanner scanner) {
    Player[] players = GameSetup.initializePlayers(scanner);

    // 初始化 Peace 和 Reversi 遊戲
    games.add(new Peace(players[0], players[1], scanner));
    games.get(0).setGameID(0);
    games.add(new Reversi(players[0], players[1], scanner));
    games.get(1).setGameID(1);
    games.get(1).canPlacePiece(players[0].pieceType);
    currentGame = 0;

    // 顯示初始棋盤
    GameView.printBoard(games.get(0), games.get(0).getBoard(), players[0], players[0], players[1]);

    while (true) {
        GameEngine engine = games.get(currentGame);
        int[] input = InputUtils.readValidInput(scanner, engine, players[engine.getCurrentPlayerIndice()].pieceType);

        // 處理退出遊戲
        if (input[1] == -2) {
            System.out.println("Exiting the game.");
            break;
        }

        // 處理新遊戲創建
        else if (input[1] == -4) {
            games.add(new Peace(players[0], players[1], scanner));
            games.get(games.size() - 1).setGameID(games.size() - 1);
            GameView.printBoard(engine, engine.getBoard(), players[engine.getCurrentPlayerIndice()], players[0], players[1]);
        } else if (input[1] == -5) {
            games.add(new Reversi(players[0], players[1], scanner));
            games.get(games.size() - 1).setGameID(games.size() - 1);
            games.get(games.size() - 1).canPlacePiece(players[0].pieceType);
            GameView.printBoard(engine, engine.getBoard(), players[engine.getCurrentPlayerIndice()], players[0], players[1]);
        }

        // 處理跳過回合
        else if (input[1] == -3) {
            if (engine instanceof Reversi) {
                if (engine.canPlacePiece(players[engine.getCurrentPlayerIndice()].pieceType)) {
                    System.out.println("You can place a piece. Please try again.");
                    continue;
                }
                engine.setCurrentPlayerIndice((engine.getCurrentPlayerIndice() + 1) % 2);
                engine.PassCounterAdd();
                engine.canPlacePiece(players[engine.getCurrentPlayerIndice()].pieceType);
            } else if (engine instanceof Peace) {
                System.out.println("You cannot pass in Peace mode. Please try again.");
                continue;
            }
            GameView.printBoard(engine, engine.getBoard(), players[engine.getCurrentPlayerIndice()], players[0], players[1]);
        }

        // 處理遊戲切換
        else if (input[1] == -1) {
            if (input[0] >= 0 && input[0] < games.size()) {
                currentGame = input[0];
            } else {
                System.out.println("Invalid game number. Please try again.");
                continue;
            }
            engine = games.get(currentGame);
            engine.canPlacePiece(players[engine.getCurrentPlayerIndice()].pieceType);
            GameView.printBoard(engine, engine.getBoard(), players[engine.getCurrentPlayerIndice()], players[0], players[1]);
        }

        // 處理棋子放置
        else {
            int col = input[0];
            int row = input[1];
            engine.placePiece(col, row, players[engine.getCurrentPlayerIndice()].pieceType);
            engine.setCurrentPlayerIndice((engine.getCurrentPlayerIndice() + 1) % 2);
            engine.canPlacePiece(players[engine.getCurrentPlayerIndice()].pieceType);
            GameView.printBoard(engine, engine.getBoard(), players[engine.getCurrentPlayerIndice()], players[0], players[1]);
        }
    }
}
```

####  設計思路

- **遊戲初始化：**
初始化玩家信息，並創建兩個遊戲（Peace 和 Reversi）。
設置每個遊戲的 GameID，方便後續管理。

- **遊戲循環：**
使用 while 循環處理玩家輸入，根據輸入執行對應的操作。

- **輸入處理：**
調用 InputUtils.readValidInput 方法解析輸入，支持多種操作：
- 退出遊戲：處理 quit 命令，結束遊戲。
- 創建新遊戲：支持創建 Peace 或 Reversi 新遊戲。
- 跳過回合：檢查當前遊戲是否允許跳過回合，並切換玩家。
- 切換遊戲：根據輸入的遊戲編號切換到對應的遊戲。
- 棋子放置：在合法位置放置棋子，並切換到下一位玩家。

- **遊戲狀態更新：**
每次操作後，調用 GameView.printBoard 更新棋盤顯示，並提示玩家下一步操作。

- **錯誤處理：**
檢查輸入是否合法（如遊戲編號是否有效、是否可以跳過回合等），並提示用戶重新輸入。



### 7. **GameEngine 的遊戲引擎接口**

```java:src/GameEngine.java
public interface GameEngine {
    Board board = new Board();
    public Player[] players = new Player[2];
    public Scanner scanner = new Scanner(System.in);
    public int currentPlayerIndice = 0;
    public int passCounter = 0;

    public int getPassCounter();

    public void PassCounterAdd();

    public static int GameID = 0;

    public int getCurrentPlayerIndice();

    public void setCurrentPlayerIndice(int currentPlayerIndice);

    public boolean isGameOver();

    public int getGameID();

    public void setGameID(int id);

    public boolean canPlacePiece(Piece piece);

    public void placePiece(int col, int row, Piece piece);

    public Board getBoard();

    public int getHowManyPieces(Piece piece);
}
```

#### 設計思路

- **統一接口設計：**
定義遊戲引擎的接口，為所有遊戲（如 Reversi 和 Peace）提供統一的操作方法。
確保不同遊戲可以通過相同的接口進行管理和操作。

- **核心方法：**
getPassCounter 和 PassCounterAdd：
用於追蹤玩家跳過回合的次數，特別是在 Reversi 中判斷遊戲是否結束。

getCurrentPlayerIndice 和 setCurrentPlayerIndice：
用於管理當前玩家的索引，實現玩家輪流操作。

isGameOver：
判斷遊戲是否結束，根據具體遊戲的規則進行實現。

canPlacePiece 和 placePiece：
檢查棋子是否可以放置，以及執行棋子放置的操作。

getHowManyPieces：
計算棋盤上某種類型棋子的數量，用於顯示分數或判斷勝負。


- **遊戲管理支持：**
getGameID 和 setGameID：
為每個遊戲分配唯一的 ID，方便在多遊戲模式下進行管理和切換。

getBoard：
提供對棋盤數據結構的訪問，支持遊戲邏輯和顯示功能。

- **擴展性：**
通過接口的設計，未來可以輕鬆添加新的遊戲類型，只需實現該接口即可。
確保代碼的模塊化和可維護性。







### 运⾏⽰例

#### 定義玩家

```bash
Please enter the first player name (Using the black piece ●): Alice
Please enter the second player name (Using the white piece ○): Bob
```


#### 初始状态

```bash
  A B C D E F G H
1 · · · · · · · ·                                                                                          
2 · · · · · · · ·                                        Game List                                         
3 · · · · · · · ·         Current Game: 1                1.  Peace                                         
4 · · · ● ○ · · ·         Player [Alice] ○               2.  Reversi                                       
5 · · · ○ ● · · ·         Player [Bob]                                                                     
6 · · · · · · · ·                                                                                          
7 · · · · · · · ·                                                                                          
8 · · · · · · · ·                                                                                          
Player Alice, please enter your move (1-8,a-h) / game number (1-2) / new game (peace / reversi) / quit the game (quit) : 
```

#### peace运⾏⽰例
- 规则同lab2。双⽅轮流在空⽩处落⼦，直⾄棋盘已满。没有记分逻辑。

```bash
  A B C D E F G H
1 · · · · · · · ·                                                                                          
2 · · · · · · · ·                                        Game List                                         
3 · · · · · · · ·         Current Game: 1                1.  Peace                                         
4 · · · ● ○ · · ·         Player [Alice] ○               2.  Reversi                                       
5 · · · ○ ● · · ·         Player [Bob]                                                                     
6 · · · · · · · ·                                                                                          
7 · · · · · · · ·                                                                                          
8 · · · · · · · ·                                                                                          
Player Alice, please enter your move (1-8,a-h) / game number (1-2) / new game (peace / reversi) / quit the game (quit) : 1a
```

```bash
  A B C D E F G H
1 ○ · · · · · · ·                                                                                          
2 · · · · · · · ·                                        Game List                                         
3 · · · · · · · ·         Current Game: 1                1.  Peace                                         
4 · · · ● ○ · · ·         Player [Alice]                 2.  Reversi                                       
5 · · · ○ ● · · ·         Player [Bob]   ●                                                                 
6 · · · · · · · ·                                                                                          
7 · · · · · · · ·                                                                                          
8 · · · · · · · ·                                                                                          
Player Bob, please enter your move (1-8,a-h) / game number (1-2) / new game (peace / reversi) / quit the game (quit) : 
```

```bash
  A B C D E F G H
1 ○ · · · · · · ·                                                                                          
2 · · · · · · · ·                                        Game List                                         
3 · · · · · · · ·         Current Game: 1                1.  Peace                                         
4 · · · ● ○ · · ·         Player [Alice]                 2.  Reversi                                       
5 · · · ○ ● · · ·         Player [Bob]   ●                                                                 
6 · · · · · · · ·                                                                                          
7 · · · · · · · ·                                                                                          
8 · · · · · · · ·                                                                                          
Player Bob, please enter your move (1-8,a-h) / game number (1-2) / new game (peace / reversi) / quit the game (quit) : 1b
```

```bash
  A B C D E F G H
1 ○ ● · · · · · ·                                                                                          
2 · · · · · · · ·                                        Game List                                         
3 · · · · · · · ·         Current Game: 1                1.  Peace                                         
4 · · · ● ○ · · ·         Player [Alice] ○               2.  Reversi                                       
5 · · · ○ ● · · ·         Player [Bob]                                                                     
6 · · · · · · · ·                                                                                          
7 · · · · · · · ·                                                                                          
8 · · · · · · · ·                                                                                          
Player Alice, please enter your move (1-8,a-h) / game number (1-2) / new game (peace / reversi) / quit the game (quit) : 
```

- 能够应对⾮法输⼊
```bash
  A B C D E F G H
1 ○ ● · · · · · ·                                                                                          
2 · · · · · · · ·                                        Game List                                         
3 · · · · · · · ·         Current Game: 1                1.  Peace                                         
4 · · · ● ○ · · ·         Player [Alice] ○               2.  Reversi                                       
5 · · · ○ ● · · ·         Player [Bob]                                                                     
6 · · · · · · · ·                                                                                          
7 · · · · · · · ·                                                                                          
8 · · · · · · · ·                                                                                          
Player Alice, please enter your move (1-8,a-h) / game number (1-2) / new game (peace / reversi) / quit the game (quit) : 1a

This position cannot be placed. Please try again.
1k
Invalid input format. Please enter a number (1-8) followed by a letter (A-H) / a board number (1-2) / new game (peace / reversi) / quit the game (quit) : 9a
Invalid input format. Please enter a number (1-8) followed by a letter (A-H) / a board number (1-2) / new game (peace / reversi) / quit the game (quit) : abc
Invalid input format. Please enter a number (1-8) followed by a letter (A-H) / a board number (1-2) / new game (peace / reversi) / quit the game (quit) : 
```


#### 切换游戏（1->2）
```bash
  A B C D E F G H
1 ○ ● · · · · · ·                                                                                          
2 · · · · · · · ·                                        Game List                                         
3 · · · · · · · ·         Current Game: 1                1.  Peace                                         
4 · · · ● ○ · · ·         Player [Alice] ○               2.  Reversi                                       
5 · · · ○ ● · · ·         Player [Bob]                                                                     
6 · · · · · · · ·                                                                                          
7 · · · · · · · ·                                                                                          
8 · · · · · · · ·                                                                                          
Player Alice, please enter your move (1-8,a-h) / game number (1-2) / new game (peace / reversi) / quit the game (quit) : 1a

This position cannot be placed. Please try again.
1k
Invalid input format. Please enter a number (1-8) followed by a letter (A-H) / a board number (1-2) / new game (peace / reversi) / quit the game (quit) : 9a
Invalid input format. Please enter a number (1-8) followed by a letter (A-H) / a board number (1-2) / new game (peace / reversi) / quit the game (quit) : abc
Invalid input format. Please enter a number (1-8) followed by a letter (A-H) / a board number (1-2) / new game (peace / reversi) / quit the game (quit) : 2
```

```bash
  A B C D E F G H
1 · · · · · · · ·                                                                                          
2 · · · · · · · ·                                        Game List                                         
3 · · · + · · · ·         Current Game: 2                1.  Peace                                         
4 · · + ● ○ · · ·         Player [Alice] ○  2            2.  Reversi                                       
5 · · · ○ ● + · ·         Player [Bob]      2                                                              
6 · · · · + · · ·                                                                                          
7 · · · · · · · ·                                                                                          
8 · · · · · · · ·                                                                                          
Player Alice, please enter your move (1-8,a-h) / game number (1-2) / new game (peace / reversi) / quit the game (quit) : 
```

#### reversi运⾏⽰例

```bash
  A B C D E F G H
1 · · · · · · · ·                                                                                          
2 · · · · · · · ·                                        Game List                                         
3 · · · + · · · ·         Current Game: 2                1.  Peace                                         
4 · · + ● ○ · · ·         Player [Alice] ○  2            2.  Reversi                                       
5 · · · ○ ● + · ·         Player [Bob]      2                                                              
6 · · · · + · · ·                                                                                          
7 · · · · · · · ·                                                                                          
8 · · · · · · · ·                                                                                          
Player Alice, please enter your move (1-8,a-h) / game number (1-2) / new game (peace / reversi) / quit the game (quit) : 3d
```

```bash
  A B C D E F G H
1 · · · · · · · ·                                                                                          
2 · · · · · · · ·                                        Game List                                         
3 · · + ○ + · · ·         Current Game: 2                1.  Peace                                         
4 · · · ○ ○ · · ·         Player [Alice]    4            2.  Reversi                                       
5 · · + ○ ● · · ·         Player [Bob]   ●  1                                                              
6 · · · · · · · ·                                                                                          
7 · · · · · · · ·                                                                                          
8 · · · · · · · ·                                                                                          
Player Bob, please enter your move (1-8,a-h) / game number (1-2) / new game (peace / reversi) / quit the game (quit) : 
```

```bash
  A B C D E F G H
1 · · · · · · · ·                                                                                          
2 · · · · · · · ·                                        Game List                                         
3 · · + ○ + · · ·         Current Game: 2                1.  Peace                                         
4 · · · ○ ○ · · ·         Player [Alice]    4            2.  Reversi                                       
5 · · + ○ ● · · ·         Player [Bob]   ●  1                                                              
6 · · · · · · · ·                                                                                          
7 · · · · · · · ·                                                                                          
8 · · · · · · · ·                                                                                          
Player Bob, please enter your move (1-8,a-h) / game number (1-2) / new game (peace / reversi) / quit the game (quit) : 3c
```

```bash
  A B C D E F G H
1 · · · · · · · ·                                                                                          
2 · · · · · · · ·                                        Game List                                         
3 · + ● ○ · · · ·         Current Game: 2                1.  Peace                                         
4 · · + ● ○ · · ·         Player [Alice] ○  3            2.  Reversi                                       
5 · · · ○ ● + · ·         Player [Bob]      3                                                              
6 · · · · + · · ·                                                                                          
7 · · · · · · · ·                                                                                          
8 · · · · · · · ·                                                                                          
Player Alice, please enter your move (1-8,a-h) / game number (1-2) / new game (peace / reversi) / quit the game (quit) : 
```

- 能够应对⾮法输⼊

```bash
  A B C D E F G H
1 · · · · · · · ·                                                                                          
2 · · · · · · · ·                                        Game List                                         
3 · + ● ○ · · · ·         Current Game: 2                1.  Peace                                         
4 · · + ● ○ · · ·         Player [Alice] ○  3            2.  Reversi                                       
5 · · · ○ ● + · ·         Player [Bob]      3                                                              
6 · · · · + · · ·                                                                                          
7 · · · · · · · ·                                                                                          
8 · · · · · · · ·                                                                                          
Player Alice, please enter your move (1-8,a-h) / game number (1-2) / new game (peace / reversi) / quit the game (quit) : 1a

This position cannot be placed. Please try again.
3c

This position cannot be placed. Please try again.
1j
Invalid input format. Please enter a number (1-8) followed by a letter (A-H) / a board number (1-2) / new game (peace / reversi) / quit the game (quit) : 9a
Invalid input format. Please enter a number (1-8) followed by a letter (A-H) / a board number (1-2) / new game (peace / reversi) / quit the game (quit) : pass

You can place a piece. Please try again.
abc
Invalid input format. Please enter a number (1-8) followed by a letter (A-H) / a board number (1-2) / new game (peace / reversi) / quit the game (quit) : 
```

#### 添加新游戏
```bash
  A B C D E F G H
1 · · · · · · · ·                                                                                          
2 · · · · · · · ·                                        Game List                                         
3 · + ● ○ · · · ·         Current Game: 2                1.  Peace                                         
4 · · + ● ○ · · ·         Player [Alice] ○  3            2.  Reversi                                       
5 · · · ○ ● + · ·         Player [Bob]      3                                                              
6 · · · · + · · ·                                                                                          
7 · · · · · · · ·                                                                                          
8 · · · · · · · ·                                                                                          
Player Alice, please enter your move (1-8,a-h) / game number (1-2) / new game (peace / reversi) / quit the game (quit) : peace
```

```bash
  A B C D E F G H
1 · · · · · · · ·                                                                                          
2 · · · · · · · ·                                        Game List                                         
3 · + ● ○ · · · ·         Current Game: 2                1.  Peace                                         
4 · · + ● ○ · · ·         Player [Alice] ○  3            2.  Reversi                                       
5 · · · ○ ● + · ·         Player [Bob]      3            3.  Peace                                         
6 · · · · + · · ·                                                                                          
7 · · · · · · · ·                                                                                          
8 · · · · · · · ·                                                                                          
Player Alice, please enter your move (1-8,a-h) / game number (1-3) / new game (peace / reversi) / quit the game (quit) : 
```

```bash
  A B C D E F G H
1 · · · · · · · ·                                                                                          
2 · · · · · · · ·                                        Game List                                         
3 · + ● ○ · · · ·         Current Game: 2                1.  Peace                                         
4 · · + ● ○ · · ·         Player [Alice] ○  3            2.  Reversi                                       
5 · · · ○ ● + · ·         Player [Bob]      3            3.  Peace                                         
6 · · · · + · · ·                                                                                          
7 · · · · · · · ·                                                                                          
8 · · · · · · · ·                                                                                          
Player Alice, please enter your move (1-8,a-h) / game number (1-3) / new game (peace / reversi) / quit the game (quit) : reversi
```

```bash
  A B C D E F G H
1 · · · · · · · ·                                                                                          
2 · · · · · · · ·                                        Game List                                         
3 · + ● ○ · · · ·         Current Game: 2                1.  Peace                                         
4 · · + ● ○ · · ·         Player [Alice] ○  3            2.  Reversi                                       
5 · · · ○ ● + · ·         Player [Bob]      3            3.  Peace                                         
6 · · · · + · · ·                                        4.  Reversi                                       
7 · · · · · · · ·                                                                                          
8 · · · · · · · ·                                                                                          
Player Alice, please enter your move (1-8,a-h) / game number (1-4) / new game (peace / reversi) / quit the game (quit) : 
```

- 切换到新游戏

```bash
  A B C D E F G H
1 · · · · · · · ·                                                                                          
2 · · · · · · · ·                                        Game List                                         
3 · + ● ○ · · · ·         Current Game: 2                1.  Peace                                         
4 · · + ● ○ · · ·         Player [Alice] ○  3            2.  Reversi                                       
5 · · · ○ ● + · ·         Player [Bob]      3            3.  Peace                                         
6 · · · · + · · ·                                        4.  Reversi                                       
7 · · · · · · · ·                                                                                          
8 · · · · · · · ·                                                                                          
Player Alice, please enter your move (1-8,a-h) / game number (1-4) / new game (peace / reversi) / quit the game (quit) : 3
```

```bash
  A B C D E F G H
1 · · · · · · · ·                                                                                          
2 · · · · · · · ·                                        Game List                                         
3 · · · · · · · ·         Current Game: 3                1.  Peace                                         
4 · · · ● ○ · · ·         Player [Alice] ○               2.  Reversi                                       
5 · · · ○ ● · · ·         Player [Bob]                   3.  Peace                                         
6 · · · · · · · ·                                        4.  Reversi                                       
7 · · · · · · · ·                                                                                          
8 · · · · · · · ·                                                                                          
Player Alice, please enter your move (1-8,a-h) / game number (1-4) / new game (peace / reversi) / quit the game (quit) : 
```

```bash
  A B C D E F G H
1 · · · · · · · ·                                                                                          
2 · · · · · · · ·                                        Game List                                         
3 · · · · · · · ·         Current Game: 3                1.  Peace                                         
4 · · · ● ○ · · ·         Player [Alice] ○               2.  Reversi                                       
5 · · · ○ ● · · ·         Player [Bob]                   3.  Peace                                         
6 · · · · · · · ·                                        4.  Reversi                                       
7 · · · · · · · ·                                                                                          
8 · · · · · · · ·                                                                                          
Player Alice, please enter your move (1-8,a-h) / game number (1-4) / new game (peace / reversi) / quit the game (quit) : 4
```

```bash
  A B C D E F G H
1 · · · · · · · ·                                                                                          
2 · · · · · · · ·                                        Game List                                         
3 · · · + · · · ·         Current Game: 4                1.  Peace                                         
4 · · + ● ○ · · ·         Player [Alice] ○  2            2.  Reversi                                       
5 · · · ○ ● + · ·         Player [Bob]      2            3.  Peace                                         
6 · · · · + · · ·                                        4.  Reversi                                       
7 · · · · · · · ·                                                                                          
8 · · · · · · · ·                                                                                          
Player Alice, please enter your move (1-8,a-h) / game number (1-4) / new game (peace / reversi) / quit the game (quit) : 
```

- 再切换回现有游戏

```bash
  A B C D E F G H
1 · · · · · · · ·                                                                                          
2 · · · · · · · ·                                        Game List                                         
3 · · · + · · · ·         Current Game: 4                1.  Peace                                         
4 · · + ● ○ · · ·         Player [Alice] ○  2            2.  Reversi                                       
5 · · · ○ ● + · ·         Player [Bob]      2            3.  Peace                                         
6 · · · · + · · ·                                        4.  Reversi                                       
7 · · · · · · · ·                                                                                          
8 · · · · · · · ·                                                                                          
Player Alice, please enter your move (1-8,a-h) / game number (1-4) / new game (peace / reversi) / quit the game (quit) : 1
```

```bash
  A B C D E F G H
1 ○ ● · · · · · ·                                                                                          
2 · · · · · · · ·                                        Game List                                         
3 · · · · · · · ·         Current Game: 1                1.  Peace                                         
4 · · · ● ○ · · ·         Player [Alice] ○               2.  Reversi                                       
5 · · · ○ ● · · ·         Player [Bob]                   3.  Peace                                         
6 · · · · · · · ·                                        4.  Reversi                                       
7 · · · · · · · ·                                                                                          
8 · · · · · · · ·                                                                                          
Player Alice, please enter your move (1-8,a-h) / game number (1-4) / new game (peace / reversi) / quit the game (quit) : 
```

```bash
  A B C D E F G H
1 ○ ● · · · · · ·                                                                                          
2 · · · · · · · ·                                        Game List                                         
3 · · · · · · · ·         Current Game: 1                1.  Peace                                         
4 · · · ● ○ · · ·         Player [Alice] ○               2.  Reversi                                       
5 · · · ○ ● · · ·         Player [Bob]                   3.  Peace                                         
6 · · · · · · · ·                                        4.  Reversi                                       
7 · · · · · · · ·                                                                                          
8 · · · · · · · ·                                                                                          
Player Alice, please enter your move (1-8,a-h) / game number (1-4) / new game (peace / reversi) / quit the game (quit) : 2
```

```bash
  A B C D E F G H
1 · · · · · · · ·                                                                                          
2 · · · · · · · ·                                        Game List                                         
3 · + ● ○ · · · ·         Current Game: 2                1.  Peace                                         
4 · · + ● ○ · · ·         Player [Alice] ○  3            2.  Reversi                                       
5 · · · ○ ● + · ·         Player [Bob]      3            3.  Peace                                         
6 · · · · + · · ·                                        4.  Reversi                                       
7 · · · · · · · ·                                                                                          
8 · · · · · · · ·                                                                                          
Player Alice, please enter your move (1-8,a-h) / game number (1-4) / new game (peace / reversi) / quit the game (quit) : 
```


#### 单局游戏的结束逻辑

- peace结束的条件：棋盘下满。

```bash
  A B C D E F G H
1 ○ ● ○ ○ ○ ○ ○ ○                                                                                          
2 ● ○ ● ● ● ● ● ●                                        Game List                                         
3 ○ ● ○ ○ ○ ○ ○ ○         Current Game: 1                1.  Peace                                         
4 ● ○ ● ● ○ ● ● ●         Player [Alice]                 2.  Reversi                                       
5 ○ ● ○ ○ ● ○ ○ ○         Player [Bob]   ●               3.  Peace                                         
6 ● ○ ● ● ● ● ● ●                                        4.  Reversi                                       
7 ○ ● ○ ○ ○ ○ ○ ○                                                                                          
8 ● ○ ● ● ● ● ● ·                                                                                          
Player Bob, please enter your move (1-8,a-h) / game number (1-4) / new game (peace / reversi) / quit the game (quit) : 8h
```

```bash
  A B C D E F G H
1 ○ ● ○ ○ ○ ○ ○ ○                                                                                          
2 ● ○ ● ● ● ● ● ●                                        Game List                                         
3 ○ ● ○ ○ ○ ○ ○ ○         Current Game: 1                1.  Peace                                         
4 ● ○ ● ● ○ ● ● ●         Player [Alice] ○               2.  Reversi                                       
5 ○ ● ○ ○ ● ○ ○ ○         Player [Bob]                   3.  Peace                                         
6 ● ○ ● ● ● ● ● ●                                        4.  Reversi                                       
7 ○ ● ○ ○ ○ ○ ○ ○                                                                                          
8 ● ○ ● ● ● ● ● ●                                                                                          
Game 1 is over now.
Please enter another board number to continue / new game (peace / reversi) / quit the game (quit) : 
```


- reversi结束的条件：棋盘下满，或者双⽅均⽆合法落⼦位置。
為方便測驗，改為4*4的棋盤

```bash
  A B C D E F G H
1 · + · ·                                                                                                  
2 + ● ○ ·                                                Game List                                         
3 · ○ ● +                 Current Game: 2                1.  Peace                                         
4 · · + ·                 Player [Alice] ○  2            2.  Reversi                                       
Player Alice, please enter your move (1-8,a-h) / game number (1-2) / new game (peace / reversi) / quit the game (quit) : 
```

```bash
  A B C D E F G H
1 · + · ·                                                                                                  
2 + ● ○ ·                                                Game List                                         
3 · ○ ● +                 Current Game: 2                1.  Peace                                         
4 · · + ·                 Player [Alice] ○  2            2.  Reversi                                       
Player Alice, please enter your move (1-8,a-h) / game number (1-2) / new game (peace / reversi) / quit the game (quit) : 1b
```

```bash
  A B C D E F G H
1 + ○ + ·                                                                                                  
2 · ○ ○ ·                                                Game List                                         
3 + ○ ● ·                 Current Game: 2                1.  Peace                                         
4 · · · ·                 Player [Alice]    4            2.  Reversi                                       
Player Bob, please enter your move (1-8,a-h) / game number (1-2) / new game (peace / reversi) / quit the game (quit) : 
```

```bash
  A B C D E F G H
1 ● ○ · ·                                                                                                  
2 ● ○ ○ ○                                                Game List                                         
3 ● ● ○ ·                 Current Game: 2                1.  Peace                                         
4 ● ● ● ·                 Player [Alice] ○  5            2.  Reversi                                       
Player Alice, you do not have place for your piece, pleace enter pass / game number (1-2) / new game (peace / reversi) / quit : 
```

```bash
  A B C D E F G H
1 ● ○ · ·                                                                                                  
2 ● ○ ○ ○                                                Game List                                         
3 ● ● ○ ·                 Current Game: 2                1.  Peace                                         
4 ● ● ● ·                 Player [Alice] ○  5            2.  Reversi                                       
Player Alice, you do not have place for your piece, pleace enter pass / game number (1-2) / new game (peace / reversi) / quit : pass
```

```bash
  A B C D E F G H
1 ● ○ + +                                                                                                  
2 ● ○ ○ ○                                                Game List                                         
3 ● ● ○ +                 Current Game: 2                1.  Peace                                         
4 ● ● ● +                 Player [Alice]    5            2.  Reversi                                       
Player Bob, please enter your move (1-8,a-h) / game number (1-2) / new game (peace / reversi) / quit the game (quit) : 
```

```bash
  A B C D E F G H
1 ● ○ + +                                                                                                  
2 ● ○ ○ ○                                                Game List                                         
3 ● ● ○ +                 Current Game: 2                1.  Peace                                         
4 ● ● ● +                 Player [Alice]    5            2.  Reversi                                       
Player Bob, please enter your move (1-8,a-h) / game number (1-2) / new game (peace / reversi) / quit the game (quit) : 4d
```

```bash
  A B C D E F G H
1 ● ○ · ·                                                                                                  
2 ● ● ○ ○                                                Game List                                         
3 ● ● ● ·                 Current Game: 2                1.  Peace                                         
4 ● ● ● ●                 Player [Alice] ○  3            2.  Reversi                                       
Player Alice, you do not have place for your piece, pleace enter pass / game number (1-2) / new game (peace / reversi) / quit : 
```


```bash
  A B C D E F G H
1 ● ○ + ●                                                                                                  
2 ● ● ○ ○                                                Game List                                         
3 ● ● ● ○                 Current Game: 2                1.  Peace                                         
4 ● ● ● ●                 Player [Alice]    4            2.  Reversi                                       
Player Bob, please enter your move (1-8,a-h) / game number (1-2) / new game (peace / reversi) / quit the game (quit) : 1c
```

```bash
  A B C D E F G H
1 ● ● ● ●                                                                                                  
2 ● ● ● ○                                                Game List                                         
3 ● ● ● ○                 Current Game: 2                1.  Peace                                         
4 ● ● ● ●                 Player [Alice] ○  2            2.  Reversi                                       
Game 2 is over now.
Player [Alice] ○    2
Player [Bob] ●    14
Player [Bob]  wins!
Please enter another board number to continue / new game (peace / reversi) / quit the game (quit) : 
```


- 双⽅均⽆合法落⼦位置。

```bash
  A B C D E F G H
1 ● ● ● ●                                                                                                  
2 ● ● ● ●                                                Game List                                         
3 ● ● ● ●                 Current Game: 2                1.  Peace                                         
4 · ○ · ·                 Player [Alice] ○  1            2.  Reversi                                       
Player Alice, you do not have place for your piece, pleace enter pass / game number (1-2) / new game (peace / reversi) / quit : 
```

```bash
  A B C D E F G H
1 ● ● ● ●                                                                                                  
2 ● ● ● ●                                                Game List                                         
3 ● ● ● ●                 Current Game: 2                1.  Peace                                         
4 · ○ · ·                 Player [Alice] ○  1            2.  Reversi                                       
Player Alice, you do not have place for your piece, pleace enter pass / game number (1-2) / new game (peace / reversi) / quit : pass
```

```bash
  A B C D E F G H
1 ● ● ● ●                                                                                                  
2 ● ● ● ●                                                Game List                                         
3 ● ● ● ●                 Current Game: 2                1.  Peace                                         
4 · ○ · ·                 Player [Alice]    1            2.  Reversi                                       
Player Bob, you do not have place for your piece, pleace enter pass / game number (1-2) / new game (peace / reversi) / quit : 
```

```bash
  A B C D E F G H
1 ● ● ● ●                                                                                                  
2 ● ● ● ●                                                Game List                                         
3 ● ● ● ●                 Current Game: 2                1.  Peace                                         
4 · ○ · ·                 Player [Alice]    1            2.  Reversi                                       
Player Bob, you do not have place for your piece, pleace enter pass / game number (1-2) / new game (peace / reversi) / quit : pass
```

```bash
  A B C D E F G H
1 ● ● ● ●                                                                                                  
2 ● ● ● ●                                                Game List                                         
3 ● ● ● ●                 Current Game: 2                1.  Peace                                         
4 · ○ · ·                 Player [Alice] ○  1            2.  Reversi                                       
Game 2 is over now.
Player [Alice] ○    1
Player [Bob] ●    12
Player [Bob]  wins!
Please enter another board number to continue / new game (peace / reversi) / quit the game (quit) : 
```


#### 即便游戏结束，依然可以切换过去查看游戏结果。

```bash
  A B C D E F G H
1 ○ ○ ○ ○ ○ ○ ○ ○                                                                                          
2 ● ● ● ● ● ● ● ●                                        Game List                                         
3 ○ ○ ○ ○ ○ ○ ○ ○         Current Game: 1                1.  Peace                                         
4 ● ● ● ● ○ ● ● ●         Player [Alice] ○               2.  Reversi                                       
5 ○ ○ ○ ○ ● ○ ○ ○         Player [Bob]                                                                     
6 ● ● ● ● ● ● ● ●                                                                                          
7 ○ ○ ○ ○ ○ ○ ○ ○                                                                                          
8 ● ● ● ● ● ● ● ●                                                                                          
Game 1 is over now.
Please enter another board number to continue / new game (peace / reversi) / quit the game (quit) : 2
```

```bash
  A B C D E F G H
1 · · · · · · · ·                                                                                          
2 · · · · · · · ·                                        Game List                                         
3 · + ● ○ · · · ·         Current Game: 2                1.  Peace                                         
4 · · + ● ○ · · ·         Player [Alice] ○  3            2.  Reversi                                       
5 · · · ○ ● + · ·         Player [Bob]      3                                                              
6 · · · · + · · ·                                                                                          
7 · · · · · · · ·                                                                                          
8 · · · · · · · ·                                                                                          
Player Alice, please enter your move (1-8,a-h) / game number (1-2) / new game (peace / reversi) / quit the game (quit) : 
```

```bash
  A B C D E F G H
1 · · · · · · · ·                                                                                          
2 · · · · · · · ·                                        Game List                                         
3 · + ● ○ · · · ·         Current Game: 2                1.  Peace                                         
4 · · + ● ○ · · ·         Player [Alice] ○  3            2.  Reversi                                       
5 · · · ○ ● + · ·         Player [Bob]      3                                                              
6 · · · · + · · ·                                                                                          
7 · · · · · · · ·                                                                                          
8 · · · · · · · ·                                                                                          
Player Alice, please enter your move (1-8,a-h) / game number (1-2) / new game (peace / reversi) / quit the game (quit) : 1
```

```bash
 A B C D E F G H
1 ○ ○ ○ ○ ○ ○ ○ ○                                                                                          
2 ● ● ● ● ● ● ● ●                                        Game List                                         
3 ○ ○ ○ ○ ○ ○ ○ ○         Current Game: 1                1.  Peace                                         
4 ● ● ● ● ○ ● ● ●         Player [Alice] ○               2.  Reversi                                       
5 ○ ○ ○ ○ ● ○ ○ ○         Player [Bob]                                                                     
6 ● ● ● ● ● ● ● ●                                                                                          
7 ○ ○ ○ ○ ○ ○ ○ ○                                                                                          
8 ● ● ● ● ● ● ● ●                                                                                          
Game 1 is over now.
Please enter another board number to continue / new game (peace / reversi) / quit the game (quit) : 
```


#### 玩家输⼊quit以退出程序。

```bash
 A B C D E F G H
1 ○ ○ ○ ○ ○ ○ ○ ○                                                                                          
2 ● ● ● ● ● ● ● ●                                        Game List                                         
3 ○ ○ ○ ○ ○ ○ ○ ○         Current Game: 1                1.  Peace                                         
4 ● ● ● ● ○ ● ● ●         Player [Alice] ○               2.  Reversi                                       
5 ○ ○ ○ ○ ● ○ ○ ○         Player [Bob]                                                                     
6 ● ● ● ● ● ● ● ●                                                                                          
7 ○ ○ ○ ○ ○ ○ ○ ○                                                                                          
8 ● ● ● ● ● ● ● ●                                                                                          
Game 1 is over now.
Please enter another board number to continue / new game (peace / reversi) / quit the game (quit) : quit
```

```bash
  A B C D E F G H
1 ○ ○ ○ ○ ○ ○ ○ ○                                                                                          
2 ● ● ● ● ● ● ● ●                                        Game List                                         
3 ○ ○ ○ ○ ○ ○ ○ ○         Current Game: 1                1.  Peace                                         
4 ● ● ● ● ○ ● ● ●         Player [Alice] ○               2.  Reversi                                       
5 ○ ○ ○ ○ ● ○ ○ ○         Player [Bob]                                                                     
6 ● ● ● ● ● ● ● ●                                                                                          
7 ○ ○ ○ ○ ○ ○ ○ ○                                                                                          
8 ● ● ● ● ● ● ● ●                                                                                          
Game 1 is over now.
Please enter another board number to continue / new game (peace / reversi) / quit the game (quit) : quit
Exiting the game.
(base) charleslam@LamdeMacBook-Pro lab2 % 
```

