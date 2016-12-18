package model;

import static model.GameConstants.SPEED_SCALE_FACTOR;

/**
 * @author apomosov
 */
public class PlayerCell extends Cell {
  private final int id;
  private int kind = 0; /**0-usual 1-ejected 2-splitted*/
  private int directionPointX, directionPointY;

  public PlayerCell(int id, int x, int y) {
    super(x, y, GameConstants.DEFAULT_PLAYER_CELL_MASS);
    this.id = id;
  }

  public int getId() {
    return id;
  }

  public int getDirectionPointX() {
    return directionPointX;
  }

  public int getDirectionPointY() {
    return directionPointY;
  }

  public int getKind() {
    return kind;
  }

  public void setKind(int kind) {
    this.kind = kind;
  }

  public void setDirectionPoint(int directionPointX, int directionPointY) {
    this.directionPointX = directionPointX;
    this.directionPointY = directionPointY;
  }

  public void calculateCoords(){
    float dx = directionPointX - x;
    float dy = directionPointY - y;
    float angle = (dy != 0)? (float) Math.atan(dx / dy) : (float)Math.PI / 2;

    if (dx > 0)
      x += (SPEED_SCALE_FACTOR / mass) * Math.abs(Math.sin(angle));
    else
      x -= (SPEED_SCALE_FACTOR / mass) * Math.abs(Math.sin(angle));
    if (dy > 0)
      y += (SPEED_SCALE_FACTOR / mass) * Math.abs(Math.cos(angle));
    else
      y -= (SPEED_SCALE_FACTOR / mass) * Math.abs(Math.cos(angle));

    x = checkCoord(x);
    y = checkCoord(y);
  }

  private int checkCoord(int coord){
    int checkedCoord;
    if (coord > GameConstants.FIELD_WIDTH) {
      checkedCoord = GameConstants.FIELD_WIDTH;
    }
    else if ( coord < 0 ){
      checkedCoord = 0;
    }
    else{
      checkedCoord = coord;
    }
    return checkedCoord;
  }
}
