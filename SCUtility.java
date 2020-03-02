/**************************************************************************
*  Compilation:  javac SCUtility.java
*  Execution:    none
*  Dependencies: SeamCarver.java
*
*  Some utility functions for testing SeamCarver.java.
*
*************************************************************************/

import java.awt.Color;


public class SCUtility {
  //transposes a picture
  public static Picture transpose(Picture picture){
    Picture transPic = new Picture(picture.height(), picture.width());
    for (int i = 0; i < picture.width(); i++){
      for (int j = 0; j < picture.height(); j++){
        transPic.setRGB(j,i, picture.getRGB(i,j));
      }
    }
    return transPic;
  }

  //returns energy of a pixel using surrounding pixel RGB values
  public static int convertEnergy(int top, int bottom, int left, int right){
    int leftR = (left >> 16) & 0xFF;
  	int leftG = (left >> 8) & 0xFF;
  	int leftB = (left >> 0) & 0xFF;

  	int rightR = (right >> 16) & 0xFF;
  	int rightG = (right >> 8) & 0xFF;
  	int rightB = (right >> 0) & 0xFF;

  	int topR = (top >> 16) & 0xFF;
  	int topG = (top >> 8) & 0xFF;
  	int topB = (top >> 0) & 0xFF;

  	int bottomR = (bottom >> 16) & 0xFF;
  	int bottomG = (bottom >> 8) & 0xFF;
  	int bottomB = (bottom >> 0) & 0xFF;

  	int xGradient = gradient((leftR - rightR),(leftG - rightG),(leftB - rightB));
  	int yGradient = gradient((topR - bottomR),(topG - bottomG),(topB - bottomB));

    return (xGradient + yGradient);
  }

  //returns gradient of three values
  public static int gradient(int x, int y, int z){
    int ans = x*x + y*y + z*z;
    return ans;
  }
  // create random W-by-H picture
  public static Picture randomPicture(int W, int H) {
    Picture picture = new Picture(W, H);
    for (int col = 0; col < W; col++) {
      for (int row = 0; row < H; row++) {
        int r = RandomUtility.uniform(256);
        int g = RandomUtility.uniform(256);
        int b = RandomUtility.uniform(256);
        Color color = new Color(r, g, b);
        picture.set(col, row, color);
      }
    }
    return picture;
  }


  // convert SeamCarver picture to W-by-H energy matrix
  public static double[][] toEnergyMatrix(SeamCarver sc) {
    double[][] a = new double[sc.width()][sc.height()];
    for (int col = 0; col < sc.width(); col++)
        for (int row = 0; row < sc.height(); row++)
            a[col][row] = sc.energy(col, row);
    return a;
  }


  // displays grayscale values as energy (converts to picture, calls show)
  public static void showEnergy(SeamCarver sc) {
    doubleToPicture(toEnergyMatrix(sc)).show();
  }


  // returns picture of energy matrix associated with SeamCarver picture
  public static Picture toEnergyPicture(SeamCarver sc) {
    double[][] energyMatrix = toEnergyMatrix(sc);
    return doubleToPicture(energyMatrix);
  }


  // This method is useful for debugging seams. It overlays red
  // pixels over the calculate seam.
  public static Picture seamOverlay(Picture picture,
                                    boolean isHorizontal, int[] seam) {
      Picture overlaid = new Picture(picture);

      //if horizontal seam, set one pixel in every column
      if (isHorizontal) {
          for (int col = 0; col < picture.width(); col++)
              overlaid.set(col, seam[col], Color.RED);
      }

      // if vertical seam, set one pixel in each row
      else {
          for (int row = 0; row < picture.height(); row++)
              overlaid.set(seam[row], row, Color.RED);
      }
      return overlaid;
  }

  // converts a double matrix of values into a normalized picture
  // values are normalized by the maximum grayscale value
  public static Picture doubleToPicture(double[][] grayValues) {

    //each 1D array in the matrix represents a single column, so number
    //of 1D arrays is the width, and length of each array is the height
    int width = grayValues.length;
    int height = grayValues[0].length;

    Picture picture = new Picture(width, height);

    double maxVal = 0;
    for (int col = 0; col < width; col++)
      for (int row = 0; row < height; row++)
        if (grayValues[col][row] > maxVal)
          maxVal = grayValues[col][row];

    if (maxVal == 0)
        return picture;  // return black picture

    for (int col = 0; col < width; col++) {
      for (int row = 0; row < height; row++) {
        float normalizedGray = (float) grayValues[col][row]
                                    / (float) maxVal;
            Color gray = new Color(normalizedGray,
                                   normalizedGray,
                                   normalizedGray);
            picture.set(col, row, gray);
        }
    }

    return picture;
  }

}
