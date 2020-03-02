public class SeamCarver{

private Picture pic;

public SeamCarver(Picture picture){
	pic = new Picture(picture);
}
public Picture picture(){
	return pic;
}
public int width(){
	return pic.width();
}
public int height(){
	return pic.height();
}
public double energy(int x, int y){
	int leftRGB = 0;
	int rightRGB = 0;
	int topRGB = 0;
	int bottomRGB = 0;

	if(x == 0){ //Dealing with pixels on left edge
		leftRGB = pic.getRGB(width()-1, y);
	} else if(x == width()-1){ //right edge
		rightRGB = pic.getRGB(0, y);
	} else{ //in the middle
		leftRGB = pic.getRGB(x-1, y);
		rightRGB = pic.getRGB(x+1, y);
	}

	if (y == 0){ //Dealing with pixels at the top
		topRGB = pic.getRGB(x, height()-1);
	} else if (y == height()-1){// at the bottom
		bottomRGB = pic.getRGB(x, 0);
	} else{ //in the middle
		topRGB = pic.getRGB(x, y-1);
		bottomRGB = pic.getRGB(x, y+1);
	}

	int energy = SCUtility.convertEnergy(topRGB, bottomRGB, leftRGB, rightRGB);
	return energy;
}
public int[] findHorizontalSeam(){
	pic = SCUtility.transpose(pic);
	int[] result = findVerticalSeam();
	pic = SCUtility.transpose(pic);
	return result;
}
public int[] findVerticalSeam(){
	double[][] energyMatrix = SCUtility.toEnergyMatrix(this);
	double[][] cMatrix = new double[width()][height()];
	int[] seamArray = new int[height()];
	double minValue = 0;
	int minIndex = 0;
	int col = 0;
	int row = 0;
	while (row < height()){ //Going down each row
		while (col < width()){ //Going through each column
			if (row == 0){
				cMatrix[col][row] = energyMatrix[col][row]; //just insert normal energies for first row
				col++;
			} else{
				if (col == 0){ //Calculating for pixels on left edge
					cMatrix[col][row] = energyMatrix[col][row] + Math.min(cMatrix[col][row-1], cMatrix[col+1][row-1]);
				} else if (col == width()-1){ //right edge
					cMatrix[col][row] = energyMatrix[col][row] + Math.min(cMatrix[col-1][row-1], cMatrix[col][row-1]);
				} else{ //pixels in the middle
					cMatrix[col][row] = energyMatrix[col][row] + Math.min(cMatrix[col-1][row-1], Math.min(cMatrix[col][row-1], cMatrix[col+1][row-1]));
				}
			}
			if (row == height()-1){
				if (col == 0){ //Saving minimum energy and index in bottom row
					minValue = cMatrix[col][row];
				} else if (cMatrix[col][row] < minValue){
					minValue = cMatrix[col][row];
					minIndex = col;
				}
			}
			col++;
		}
		row++;
		col = 0;
	}
	row--; //Setting row to last index
	while (row >= 0){ //Going back up
		if (row != 0){
			if (minIndex == 0){
				if (cMatrix[minIndex][row-1] > cMatrix[minIndex+1][row-1]){
					minIndex ++;
				}
			} else if (minIndex == width()-1){
				if (cMatrix[minIndex][row-1] > cMatrix[minIndex-1][row-1]){
					minIndex --;
				}
			} else{
				if (cMatrix[minIndex-1][row-1] < cMatrix[minIndex][row-1] || cMatrix[minIndex+1][row] < cMatrix[minIndex][row-1]){
					if (cMatrix[minIndex-1][row-1] > cMatrix[minIndex + 1][row]){
						minIndex ++;
					} else{
						minIndex --;
					}
				}
			}
		}
		seamArray[row] = minIndex;
		row--;
	}
	return seamArray;
}

public void removeHorizontalSeam(int[] seam){
	Picture newPic = new Picture(width(), height()-1);
	int row = 0;
	int col = 0;
	int newRow = 0;
	while (col < width()){
		while (row < height()){
			if (row != seam[col]){
				newPic.set(col, newRow, this.pic.get(col, row));;
				newRow++;
			}
			row++;
		}
		col++;
		row = 0;
		newRow = 0;
	}
	this.pic = newPic;
	return;
}
public void removeVerticalSeam(int[] seam){
	Picture newPic = new Picture(width()-1, height());
	int row = 0;
	int col = 0;
	int newCol = 0;
	while (row < height()){
		while (col < width()){
			if (col != seam[row]){
				newPic.set(newCol, row, this.pic.get(col, row));
				newCol++;
			}
			col++;
		}
		row++;
		col = 0;
		newCol = 0;
	}
	this.pic = newPic;
	return;
}
}
