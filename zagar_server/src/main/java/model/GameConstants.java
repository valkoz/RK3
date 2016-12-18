package model;

/**
 * @author apomosov
 */
public interface GameConstants {
  int MAX_PLAYERS_IN_SESSION = 10;
  int FIELD_WIDTH = 2000;
  int FIELD_HEIGHT = 2000;
  int FOOD_MASS = 20;
  int DEFAULT_PLAYER_CELL_MASS = 40;
  int VIRUS_MASS = 50;
  int FOOD_PER_SECOND_GENERATION = 1;
  int MAX_FOOD_ON_FIELD = 100;
  int NUMBER_OF_VIRUSES = 10;
  float PORTION_OF_FOODMASS_EATEN = 0.1f;
  int FOOD_COUNT = 100;
  int SPEED_SCALE_FACTOR = 2000;
  int EJECTED_MASS = 40;
  int EJECT_DISTANCE_SCALE = 60;
}
