package storage;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Environment;
import android.provider.BaseColumns;
import android.util.Log;

import com.dhiren.reseplebaran.R;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import model.RecipeModel;

public class RecipeBookXls {
    private Context mContext;
    // Create a POIFSFileSystem object
    POIFSFileSystem mFs;
    // Create a workbook using the File System
    HSSFWorkbook mWorkbook;

    public Cursor searchRecipe(String like) {
        MatrixCursor cursor = new MatrixCursor(new String[]{BaseColumns._ID, "name", "img", "cat_id", "rec_id"});
        int count = 0;
        for (int cat=0; cat<getCategoryCount(); cat++) {
            for (int rec=0; rec<getRecipeCount(cat); rec++) {
                RecipeModel recipeModel = getRecipe(cat,rec);
                if (recipeModel.recipeName.toLowerCase().contains(like.toLowerCase())) {
                    Object[] row = {count++, recipeModel.recipeName, recipeModel.imgUrl, recipeModel.categoryId, recipeModel.recipeId};
                    cursor.addRow(row);
                }
            }
        }
        return cursor;
    }

    public RecipeBookXls(Context context) {
        mContext = context;
        readRecipeBook();
    }

    public void readRecipeBook() {
        readExcelFile();
    }


    public RecipeModel getRandomRecipe() {
        int cat = rand(0, getCategoryCount());
        int rec = rand(0, getRecipeCount(cat));
        return getRecipe(cat, rec);
    }

    private int rand(int min, int max) {
        Random random = new Random();
        int randomNumber = random.nextInt(max - min) + min;
        return randomNumber;
    }

    public ArrayList<RecipeModel> getCategories() {
        ArrayList<RecipeModel> recipeModels = new ArrayList<>();
        int catCount;
        if ((catCount = getCategoryCount()) > 0) {
            for (int i = 0; i < catCount; i++) {
                recipeModels.add(getRecipe(i, 0));
            }
            return recipeModels;
        }
        return null;
    }

    public RecipeModel getRecipe(int categoryId, int recipeId) {
        if (categoryId < getCategoryCount()) {
            RecipeModel recipeModel = new RecipeModel("","","",0,0);
            recipeModel.categoryName = mWorkbook.getSheetAt(categoryId).getSheetName();
            recipeModel.categoryId = categoryId;
            recipeModel.recipeId = recipeId;
            HSSFSheet sh = mWorkbook.getSheetAt(categoryId);
            HSSFRow rw = sh.getRow(1);
            if (rw != null) {
                HSSFCell cl = rw.getCell(recipeId + 1);
                if (cl == null) return null;
                recipeModel.imgUrl = cl.getStringCellValue();

                rw = sh.getRow(2);
                if (rw == null) return null;
                cl = rw.getCell(recipeId + 1);
                if (cl == null) return null;
                recipeModel.recipeName = cl.getStringCellValue();

                rw = sh.getRow(3);
                if (rw == null) return null;
                cl = rw.getCell(recipeId + 1);
                if (cl == null) return null;
                recipeModel.ingredients = cl.getStringCellValue();

                rw = sh.getRow(4);
                if (rw == null) return null;
                cl = rw.getCell(recipeId + 1);
                if (cl == null) return null;
                recipeModel.howto = cl.getStringCellValue();
            }

            return recipeModel;
        }
        return null;
    }

    public ArrayList<RecipeModel> getRecipes(int categoryId) {
        if (categoryId < getCategoryCount()) {
            int recipeCount = getRecipeCount(categoryId);
            ArrayList<RecipeModel> recipeModels = new ArrayList<>();
            for (int i = 0; i < recipeCount; i++) {
                recipeModels.add(getRecipe(categoryId, i));
            }
            return recipeModels;
        }
        return null;
    }

    public int getRecipeCount(int categoryId) {
        if (categoryId < getCategoryCount()) {
            int recipeCount = 0;
            // Get the first sheet from workbook
            HSSFSheet mySheet = mWorkbook.getSheetAt(categoryId);

            /** We now need something to iterate through the cells.**/
            Row row = mySheet.getRow(3);
            if (row != null) {
                int count = 0;
                Iterator<Cell> cellIter = row.cellIterator();
                if (cellIter.hasNext()) {
                    cellIter.next(); // skip header
                    while (cellIter.hasNext()) {
                        HSSFCell c = (HSSFCell) cellIter.next();
                        if (c.getStringCellValue().length() > 0) {
                            count++;
                        }
                        else {
                            break;
                        }
                    }
                }

                return count;
            } else {
                return -1;
            }
        } else {
            return -1;
        }
    }

    public int getCategoryCount() {
        return mWorkbook.getNumberOfSheets();
    }

    private void readExcelFile() {

        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            Log.w("FileUtils", "Storage not available or read only");
            return;
        }

        try {
            // Create a POIFSFileSystem object
            mFs = new POIFSFileSystem(mContext.getResources().openRawResource(R.raw.recipes));

            // Create a workbook using the File System
            mWorkbook = new HSSFWorkbook(mFs);
            Log.d("XLS", "Read OK");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return;
    }


    public static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    public static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;
    }
}
