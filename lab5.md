# 实验报告：Reversi、Peace 和 Gomoku（示例） 游戏系统

## 执行 Java 程式
```bash
java -cp bin App
```

## 项目说明
本项目在原先的多游戏系统基础上新增了 Gomoku（五子棋）游戏，实现了以下功能拓展：

- 支持 Peace、Reversi、Gomoku 多种游戏。
- 游玩各个游戏时可以进行自由切换，并同时管理多个棋盘。
- 新增 `InputUtils` 针对 Gomoku 的输入校验逻辑，以及 `Gomoku.java` 内的专用胜负判断。
- 其他功能与 Lab 4 相同，仍然支持玩家创建新游戏、放置棋子、跳过（如适用）、退出等交互操作。

## 新增内容说明

### 1. **Gomoku.java**
- 实现了五子棋的核心逻辑，包括棋子放置、胜负判断（连续五子）。
- 使用 `isLine` 方法判断是否形成五子连线。

### 2. **InputUtils.java**
- 更新输入校验逻辑，支持 Gomoku 游戏的输入解析。
- 确保输入合法性并提示玩家重新输入。

### 3. **GameManager.java**
- 支持动态创建 Gomoku 游戏实例。
- 管理多个游戏的切换逻辑。

### 4. **GameView.java**
- 更新棋盘显示逻辑，支持 Gomoku 游戏的棋盘展示。
- 在游戏结束时显示胜负结果。

## 设计思路与关键代码

### 1. 胜负判断逻辑
通过方向数组遍历八个方向，检查是否有连续五个相同棋子：
```java
// filepath: /src/Gomoku.java
public void isLine(int col, int row, Piece piece) {
    for (int[] direction : directions) {
        int count = 1;
        int dx = direction[0];
        int dy = direction[1];

        for (int i = 1; i < 5; i++) {
            int newCol = col + dx * i;
            int newRow = row + dy * i;

            if (newCol < 0 || newCol >= Board.SIZE || newRow < 0 || newRow >= Board.SIZE) {
                break;
            }

            if (board.getWhatPiece(newCol, newRow) == piece) {
                count++;
            } else {
                break;
            }
        }

        if (count >= 5) {
            haveLine = true;
            break;
        }
    }
}
```

### 2. 输入校验逻辑
解析用户输入并验证合法性：
```java
// filepath: /src/InputUtils.java
public static int[] parseInput(String input) throws IllegalArgumentException {
    Matcher matcher = INPUT_PATTERN.matcher(input);
    if (!matcher.find()) {
        throw new IllegalArgumentException("Invalid input format");
    }

    int row = Integer.parseInt(matcher.group(1)) - 1;
    int col = Character.toLowerCase(matcher.group(2).charAt(0)) - 'a';

    return new int[] { row, col };
}
```

### 3. 多游戏管理
动态创建新游戏并切换：
```java
// filepath: /src/GameManager.java
public static void start(Scanner scanner) {
    games.add(new Gomoku(players[0], players[1], scanner));
    games.get(2).setGameID(2);
    GameView.printBoard(games.get(2), games.get(2).getBoard(), players[0], players[0], players[1]);
}
```

## 运行过程截图

### 1. 開始遊戲
```
  A B C D E F G H
1 · · · · · · · ·                                                                                          
2 · · · · · · · ·                                        Game List                                         
3 · · · · · · · ·         Current Game: 1                1.  Peace                                         
4 · · · ● ○ · · ·         Player [Tom]   ○               2.  Reversi                                       
5 · · · ○ ● · · ·         Player [Jerry]                 3.  Gomoku                                        
6 · · · · · · · ·                                                                                          
7 · · · · · · · ·                                                                                          
8 · · · · · · · ·                                                                                          
Player Tom, please enter your move (1-8,a-h) / game number (1-3) / new game (peace / reversi / gomoku) / quit the game (quit) : 
```

### 2. 跳轉到初始化了的 Gomoku
```
  A B C D E F G H
1 · · · · · · · ·                                                                                          
2 · · · · · · · ·                                        Game List                                         
3 · · · · · · · ·         Current Game: 3                1.  Peace                                         
4 · · · · · · · ·         Player [Tom]   ○               2.  Reversi                                       
5 · · · · · · · ·         Player [Jerry]                 3.  Gomoku                                        
6 · · · · · · · ·         Current Round : 1                                                                
7 · · · · · · · ·                                                                                          
8 · · · · · · · ·                                                                                          
Player Tom, please enter your move (1-8,a-h) / game number (1-3) / new game (peace / reversi / gomoku) / quit the game (quit) : 
```

### 3. 連成一線
```
  A B C D E F G H
1 ○ ○ ● · · · · ·                                                                                          
2 ● ● ● ○ ○ · · ·                                        Game List                                         
3 ○ ○ ○ ● · · · ·         Current Game: 3                1.  Peace                                         
4 ● ● ○ · ○ ● · ○         Player [Tom]   ○               2.  Reversi                                       
5 ● ○ ● · ● · · ●         Player [Jerry]                 3.  Gomoku                                        
6 ○ ● ○ · · · · ·         Current Round : 16                                                                
7 ● ○ · · · · · ·                                                                                          
8 ○ ● · · · · · ·                                                                                         
Player Tom, please enter your move (1-8,a-h) / game number (1-3) / new game (peace / reversi / gomoku) / quit the game (quit) : 5d
```

### 4. 遊戲結束，並且無法再輸入
```
  A B C D E F G H
1 ○ ○ ● · · · · ·                                                                                          
2 ● ● ● ○ ○ · · ·                                        Game List                                         
3 ○ ○ ○ ● · · · ·         Current Game: 3                1.  Peace                                         
4 ● ● ○ · ○ ● · ○         Player [Tom]                   2.  Reversi                                       
5 ● ○ ● ○ ● · · ●         Player [Jerry] ●               3.  Gomoku                                        
6 ○ ● ○ · · · · ·         Current Round : 16                                                               
7 ● ○ · · · · · ·                                                                                          
8 ○ ● · · · · · ·                                                                                          
Game 3 is over now. Player [Tom] wins!
Please enter another board number to continue / new game (peace / reversi / gomoku) / quit the game (quit) : 7c
This position cannot be placed. Please try again.
```

### 5. 開啟新的棋盤
```
  A B C D E F G H
1 ○ ○ ● · · · · ·                                                                                          
2 ● ● ● ○ ○ · · ·                                        Game List                                         
3 ○ ○ ○ ● · · · ·         Current Game: 3                1.  Peace                                         
4 ● ● ○ · ○ ● · ○         Player [Tom]                   2.  Reversi                                       
5 ● ○ ● ○ ● · · ●         Player [Jerry] ●               3.  Gomoku                                        
6 ○ ● ○ · · · · ·         Current Round : 16                                                     
7 ● ○ · · · · · ·                                                                                          
8 ○ ● · · · · · ·                                                                                          
Game 3 is over now. Player [Tom] wins!
Please enter another board number to continue / new game (peace / reversi / gomoku) / quit the game (quit) : 7c
This position cannot be placed. Please try again. Gomoku
```

### 6. 跳轉到新開的棋盤
```
  A B C D E F G H
1 ○ ○ ● · · · · ·                                                                                          
2 ● ● ● ○ ○ · · ·                                        Game List                                         
3 ○ ○ ○ ● · · · ·         Current Game: 3                1.  Peace                                         
4 ● ● ○ · ○ ● · ○         Player [Tom]                   2.  Reversi                                       
5 ● ○ ● ○ ● · · ●         Player [Jerry] ●               3.  Gomoku                                        
6 ○ ● ○ · · · · ·         Current Round : 16             4.  Gomoku                                        
7 ● ○ · · · · · ·                                                                                          
8 ○ ● · · · · · ·                                                                                          
Game 3 is over now. Player [Tom] wins!
Please enter another board number to continue / new game (peace / reversi / gomoku) / quit the game (quit) : 4
```

### 7. 沒有連成一線，棋盤將滿
```
  A B C D E F G H
1 ○ ○ ○ ○ ● ● ● ●                                                                                          
2 ● ● ● ● ○ ○ ○ ○                                        Game List                                         
3 ○ ○ ○ ○ ● ● ● ●         Current Game: 4                1.  Peace                               
4 ● ● ● ● ○ ○ ○ ○         Player [Tom]                   2.  Reversi                                       
5 ○ ○ ○ ○ ● ● ● ●         Player [Jerry] ●               3.  Gomoku                                        
6 ● ● ● ● ○ ○ ○ ○         Current Round : 32             4.  Gomoku                                        
7 ○ ○ ○ ○ ● ● ● ·                                                                                
8 ● ● ● ● ○ ○ ○ ○                                                                               
Player Jerry, please enter your move (1-8,a-h) / game number (1-7) / new game (peace / reversi / gomoku) / quit the game (quit) : 7h
```

### 8. 平手並且跳轉回 3 號棋盤
```
  A B C D E F G H
1 ○ ○ ○ ○ ● ● ● ●                                                                                          
2 ● ● ● ● ○ ○ ○ ○                                        Game List                                         
3 ○ ○ ○ ○ ● ● ● ●         Current Game: 4                1.  Peace                             
4 ● ● ● ● ○ ○ ○ ○         Player [Tom]   ○               2.  Reversi                                       
5 ○ ○ ○ ○ ● ● ● ●         Player [Jerry]                 3.  Gomoku                                        
6 ● ● ● ● ○ ○ ○ ○         Current Round : 33             4.  Gomoku                                        
7 ○ ○ ○ ○ ● ● ● ●                                                                             
8 ● ● ● ● ○ ○ ○ ○                                                                             
Game 6 is over now. It's a tie!
Please enter another board number to continue / new game (peace / reversi / gomoku) / quit the game (quit) :  3
```

### 9. 返回 3 號棋盤
```
  A B C D E F G H
1 ○ ○ ● · · · · ·                                                                                          
2 ● ● ● ○ ○ · · ·                                        Game List                                         
3 ○ ○ ○ ● · · · ·         Current Game: 3                1.  Peace                                         
4 ● ● ○ · ○ ● · ○         Player [Tom]                   2.  Reversi                                       
5 ● ○ ● ○ ● · · ●         Player [Jerry] ●               3.  Gomoku                                        
6 ○ ● ○ · · · · ·         Current Round : 16             4.  Gomoku                                        
7 ● ○ · · · · · ·                                                                                          
8 ○ ● · · · · · ·                                                                                          
Game 3 is over now. Player [Tom] wins!
Please enter another board number to continue / new game (peace / reversi / gomoku) / quit the game (quit) :