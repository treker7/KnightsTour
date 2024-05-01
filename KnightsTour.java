/*
http://staff.ustc.edu.cn/~yuzhang/ds/samples/KnightTour1/doc/overview.html
It can be proven that there is a closed knight's tour on all boards n x n with an even number of squares and dimensions greater than four.

GREAT BOOK ON EAs: http://books.google.com/books?id=gwUwIEPqk30C&pg=PA450&lpg=PA450&dq=knights+tour+difficulty+level&source=bl&ots=GJs1AkObi7&sig=89dguLRho41D67_3w3Cx5rf7iUE&hl=en&sa=X&ei=I5YnU4qLDMiPrgGO-YFw&ved=0CFAQ6AEwBg#v=onepage&q=knights%20tour%20difficulty%20level&f=false
*/

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class KnightsTour extends JFrame{
   public static final int DELAY = 200;//in milliseconds
   public static final int CELL_SIZE = 100;//in pixels
   public static final int BOARD_SIZE = 8;//NOTE: BOARD_SIZE MUST be EVEN and greater than FIVE, in order to guarantee a solution
   public static final int[] MOVES = {2, 1, 1, 2, -1, 2, -2, 1, -2, -1, -1, -2, 1, -2, 2, -1};//Movement (of a knight) (y and x coords)
   private int[] moves = new int[BOARD_SIZE * BOARD_SIZE * 2];//y and x coords of knights tour
   private boolean[][] visited = new boolean[BOARD_SIZE][BOARD_SIZE];
   private int moveNum = 0;
   private Timer time;
   
   BoardPanel bPanel = new BoardPanel();
      
   public KnightsTour(){
      if(!start()){
         JOptionPane.showMessageDialog(null, "Failed to find a path!");
      }//initialize knight to random position and find path of tour
                
      this.add(bPanel, BorderLayout.CENTER);
      
      this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      this.setTitle("Knight's Tour");
      this.setLocation(0, 0);
      this.setSize(CELL_SIZE * BOARD_SIZE + 6, CELL_SIZE * BOARD_SIZE + 29);
      this.setResizable(false);
      this.setVisible(true);
      
      time = new Timer(DELAY, new ActionListener(){
         public void actionPerformed(ActionEvent e){
            if(moveNum < (BOARD_SIZE * BOARD_SIZE)){//we haven't yet completed the tour.
               bPanel.move(moves[2 * moveNum], moves[2 * moveNum + 1]);
               moveNum++;    
            }
         }
      });
      time.start();      
   }
   private boolean start(){
      moves[0] = (int)(Math.random() * BOARD_SIZE);
      moves[1] = (int)(Math.random() * BOARD_SIZE);
      System.out.println(moves[0] + "   " + moves[1]);
      moveNum = 1;
      for(int t = 0; t < visited.length; t++){
         for(int t2 = 0; t2 < visited[0].length; t2++){
            visited[t][t2] = false;
         }
      }
      visited[moves[0]][moves[1]] = true;
      bPanel.move(moves[0], moves[1]);
      return searchMovesDepthFirst(moves[0], moves[1], moveNum);//fill the move array with valid moves
   }
   public boolean searchMovesDepthFirst(int fromY, int fromX, int depth){//this method will eventually fill the moves array with coords
      if(depth == BOARD_SIZE * BOARD_SIZE){//success (end of recursion)
         return true;
      }else{
         int[] availMoves = getAvailMoves(fromY, fromX);//get available moves for this coordinate and stage
         if(availMoves.length != 0){
            for(int branches = 0; branches < availMoves.length; branches += 2){
               moves[depth * 2] = availMoves[branches];
               moves[depth * 2 + 1] = availMoves[branches + 1];
               visited[moves[depth * 2]][moves[depth * 2 + 1]] = true;
               if(searchMovesDepthFirst(moves[depth * 2], moves[depth * 2 + 1], depth + 1)){//recursive CALL
                  return true;
               }
               visited[moves[depth * 2]][moves[depth * 2 + 1]] = false;   
            }
            return false;//all options failed, so go back up a level
         }
         else{return false;/*dead end (no where to move to) Go back up a level of depth and keep trying*/} 
      }      
   }
   //the following effectively uses Warnsdorff's rule (move to square with fewest possible next moves)
   private int[] getAvailMoves(int fromY, int fromX){//NOTE: MOVES should be ordered by least possible NEXT moves from that possition
      int[] tempC = new int[16];
      int tempY, tempX;
      int size = 0;
            
      for(int t = 0; t < MOVES.length; t += 2){
         tempY = fromY + MOVES[t];
         tempX = fromX + MOVES[t + 1];
         if(tempY >= 0 && tempX >= 0 && tempY < BOARD_SIZE && tempX < BOARD_SIZE && !(visited[tempY][tempX])){//bounds checking
            tempC[t] = tempY;
            tempC[t + 1] = tempX;
            size += 2;
         }else{
            tempC[t] = -1;
            tempC[t + 1] = -1;
         }
      }            
      
      int[] r = new int[size];
      int t = 0;
      for(int i = 0; i < tempC.length; i++){//copy non-negative coords of tempC to r[]
         if(tempC[i] != -1){
            r[t] = tempC[i];
            t++;
         }
      }
      //now order the moves using selection sort
      int movesAfterPossibleMoves[] =  new int[r.length / 2];
      int temp;
      for(int h = 0; h < movesAfterPossibleMoves.length; h++){
         movesAfterPossibleMoves[h] = getNumPossibleMoves(r[h * 2], r[h * 2 + 1]);
      }      
      for(int g = 0; g < movesAfterPossibleMoves.length; g++){
         int leastI = g;
         for(int currI = g + 1; currI < movesAfterPossibleMoves.length; currI++){//compare to get index of least
            if(movesAfterPossibleMoves[currI] < movesAfterPossibleMoves[leastI]){
               leastI = currI;
            }
         }
         //do swap
         temp = r[g * 2];
         r[g * 2] = r[leastI * 2];
         r[leastI * 2] = temp;
         
         temp = r[g * 2 + 1];
         r[g * 2 + 1] = r[leastI * 2 + 1];
         r[leastI * 2 + 1] = temp;         
      }      
      
      return r;
   }
   private int getNumPossibleMoves(int y, int x){
      int tempX, tempY;
      int moves = 0;
      for(int t = 0; t < MOVES.length; t += 2){//iterates 8 times
         tempY = y + MOVES[t];
         tempX = x + MOVES[t + 1];
         if(tempY >= 0 && tempX >= 0 && tempY < BOARD_SIZE && tempX < BOARD_SIZE && !(visited[tempY][tempX])){//bounds checking
            moves++;
         }
      }
      return moves;   
   }
         
   private class BoardPanel extends JPanel{
      private Color whiteS = new Color(180, 180, 180), blackS = new Color(0, 140, 0);
      private Image knight = new ImageIcon("chess_piece_black_knight.png").getImage();
      private boolean[][] board = new boolean[BOARD_SIZE][BOARD_SIZE];//true = a visited cell
      int y, x;//knight's position
            
      public BoardPanel(){
        
      }
      public void move(int y, int x){
         board[y][x] = true;
         this.y = y;
         this.x = x;
         repaint();
      }
      public void clear(){
         board = new boolean[BOARD_SIZE][BOARD_SIZE];
      }
      public void paintComponent(Graphics g){
         super.paintComponent(g);
         boolean isWhite = true;
         for(int y = 0; y < BOARD_SIZE; y++){
            for(int x = 0; x < BOARD_SIZE; x++){
               if(board[y][x]){//black for visited square
                  g.setColor(Color.BLACK);
                  g.fillRect(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
               }else{
                  if(isWhite){g.setColor(whiteS);}
                  else{g.setColor(blackS);}
                  g.fillRect(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
               }
               isWhite = !isWhite;
            }
            if(BOARD_SIZE % 2 == 0){isWhite = !isWhite;}//if even side length on board, alternate colors again
         }
         g.drawImage(knight, x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE, this);
      }
   }
   public static void main(String[] args){
      new KnightsTour();
   }
}