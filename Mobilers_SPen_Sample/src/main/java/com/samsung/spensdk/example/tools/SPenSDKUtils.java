package com.samsung.spensdk.example.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.widget.ImageView;

import com.samsung.spensdk.SCanvasConstants;
import com.samsung.spensdk.SCanvasView;

import de.mobilers.android.spensample.R;

public class SPenSDKUtils {

    /**
     * Get the real file path from URI registered in media store
     * @param contentUri URI registered in media store
     */
    public static   String getRealPathFromURI(Activity activity, Uri contentUri) {

        String releaseNumber = Build.VERSION.RELEASE;

        if(releaseNumber!=null){
			/* ICS, JB Version */
            if(releaseNumber.length()>0 && releaseNumber.charAt(0)=='4'){
                // URI from Gallery(MediaStore)
                String[] proj = { MediaStore.Images.Media.DATA };
                String strFileName="";
                CursorLoader cursorLoader = new CursorLoader(activity, contentUri, proj, null, null,null);
                Cursor cursor = cursorLoader.loadInBackground();
                if(cursor!=null)
                {
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    cursor.moveToFirst();
                    if(cursor.getCount()>0)
                        strFileName=cursor.getString(column_index);

                    cursor.close();
                }
                // URI from Others(Dropbox, etc.)
                if(strFileName==null || strFileName.isEmpty()){
                    if(contentUri.getScheme().compareTo("file")==0)
                        strFileName = contentUri.getPath();
                }
                return strFileName;
            }
			/* GB Version */
            else if(releaseNumber.startsWith("2.3")){
                // URI from Gallery(MediaStore)
                String[] proj = { MediaStore.Images.Media.DATA };
                String strFileName="";
                Cursor cursor = activity.managedQuery(contentUri, proj, null, null, null);
                if(cursor!=null)
                {
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

                    cursor.moveToFirst();
                    if(cursor.getCount()>0)
                        strFileName=cursor.getString(column_index);

                    cursor.close();
                }
                // URI from Others(Dropbox, etc.)
                if(strFileName==null || strFileName.isEmpty()){
                    if(contentUri.getScheme().compareTo("file")==0)
                        strFileName = contentUri.getPath();
                }
                return strFileName;
            }
        }

        //---------------------
        // Undefined Version
        //---------------------
		/* GB, ICS Common */
        String[] proj = { MediaStore.Images.Media.DATA };
        String strFileName="";
        Cursor cursor = activity.managedQuery(contentUri, proj, null, null, null);
        if(cursor!=null)
        {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

            // Use the Cursor manager in ICS
            activity.startManagingCursor(cursor);

            cursor.moveToFirst();
            if(cursor.getCount()>0)
                strFileName=cursor.getString(column_index);

            //cursor.close(); // If the cursor close use , This application is terminated .(In ICS Version)
            activity.stopManagingCursor(cursor);
        }
        return strFileName;
    }


    /**
     * Save jpeg image with background color
     * @param strFileName Save file path
     * @param bitmap Input bitmap
     * @param nQuality Jpeg quality for saving
     * @param nBackgroundColor background color
     * @return whether success or not
     */
    public static boolean saveBitmapJPEGWithBackgroundColor(String strFileName, Bitmap bitmap, int nQuality, int nBackgroundColor)
    {
        boolean bSuccess1 = false;
        boolean bSuccess2 = false;
        boolean bSuccess3;
        File saveFile = new File(strFileName);

        if(saveFile.exists()) {
            if(!saveFile.delete())
                return false;
        }

        int nA = (nBackgroundColor>>24)&0xff;

        // If Background color alpha is 0, Background color substitutes as white
        if(nA==0)
            nBackgroundColor = 0xFFFFFFFF;

        Rect rect = new Rect(0,0,bitmap.getWidth(), bitmap.getHeight());
        Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawColor(nBackgroundColor);
        canvas.drawBitmap(bitmap, rect, rect, new Paint());

        // Quality limitation min/max
        if(nQuality<10) nQuality = 10;
        else if(nQuality>100) nQuality = 100;

        OutputStream out = null;

        try {
            bSuccess1 = saveFile.createNewFile();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        try {
            out = new FileOutputStream(saveFile);
            bSuccess2 = newBitmap.compress(CompressFormat.JPEG, nQuality, out);
        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            if(out!=null)
            {
                out.flush();
                out.close();
                bSuccess3 = true;
            }
            else
                bSuccess3 = false;

        } catch (IOException e) {
            e.printStackTrace();
            bSuccess3 = false;
        }finally
        {
            if(out != null)
            {
                try {
                    out.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        return (bSuccess1 && bSuccess2 && bSuccess3);
    }



    /**
     * Save PNG image with background color
     * @param strFileName Save file path
     * @param bitmap Input bitmap
     * @param nQuality Jpeg quality for saving
     * @param nBackgroundColor background color
     * @return whether success or not
     */
    public static boolean saveBitmapPNGWithBackgroundColor(String strFileName, Bitmap bitmap, int nBackgroundColor)
    {
        boolean bSuccess1 = false;
        boolean bSuccess2 = false;
        boolean bSuccess3;
        File saveFile = new File(strFileName);

        if(saveFile.exists()) {
            if(!saveFile.delete())
                return false;
        }

        int nA = (nBackgroundColor>>24)&0xff;

        // If Background color alpha is 0, Background color substitutes as white
        if(nA==0)
            nBackgroundColor = 0xFFFFFFFF;

        Rect rect = new Rect(0,0,bitmap.getWidth(), bitmap.getHeight());
        Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawColor(nBackgroundColor);
        canvas.drawBitmap(bitmap, rect, rect, new Paint());

        OutputStream out = null;

        try {
            bSuccess1 = saveFile.createNewFile();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        try {
            out = new FileOutputStream(saveFile);
            bSuccess2 = newBitmap.compress(CompressFormat.PNG, 100, out);
        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            if(out!=null)
            {
                out.flush();
                out.close();
                bSuccess3 = true;
            }
            else
                bSuccess3 = false;

        } catch (IOException e) {
            e.printStackTrace();
            bSuccess3 = false;
        }finally
        {
            if(out != null)
            {
                try {
                    out.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        return (bSuccess1 && bSuccess2 && bSuccess3);
    }

    // Check whether valid image file or not
    public static boolean isValidImagePath(String strImagePath){
        if(strImagePath==null){
            return false;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(strImagePath, options);

        return (options.outMimeType != null);
    }

    // Check whether valid file name or not
    public static  boolean isValidSaveName(String fileName) {

        int len = fileName.length();
        for (int i = 0; i < len; i++) {
            char c = fileName.charAt(i);

            if(c== '\\'|| c== ':' || c== '/' || c== '*' || c== '?' || c== '"'
                    || c== '<' || c== '>' || c== '|' || c== '\t'|| c== '\n') {
                return false;
            }
        }
        return true;
    }

    /****************************************************************************************************************
     * Get the image bitmap that resizing to maximum size of limit.
     * Parameter :
     *  - context : Context
     *  - uri : Image URI
     *  - bContentStreamImage : Gallery contents stream file(true)/file path(false)
     *  - nMaxResizedWidth : The maximum allowable width of resizing image.
     *  - nMaxResizedHeight : The maximum allowable height of resizing image.
     * Return :
     *  - Resizing bitmap
     */
    public static Bitmap getSafeResizingBitmap(
            String strImagePath,
            int nMaxResizedWidth,
            int nMaxResizedHeight,
            boolean checkOrientation)
    {
        //==========================================
        // Bitmap Option
        //==========================================
        BitmapFactory.Options options = getBitmapSize(strImagePath);
        if(options == null)
            return null;

        //==========================================
        // Bitmap Scaling
        //==========================================
        int nSampleSize;
        int degree = 0;
        if(checkOrientation){
            degree = getExifDegree(strImagePath);
        }

        if(degree==90 || degree==270){
            nSampleSize = getSafeResizingSampleSize(options.outHeight, options.outWidth, nMaxResizedWidth, nMaxResizedHeight);
        }
        else{
            nSampleSize = getSafeResizingSampleSize(options.outWidth, options.outHeight, nMaxResizedWidth, nMaxResizedHeight);
        }


        //==========================================
        // Load the bitmap including actually data.
        //==========================================
        options.inJustDecodeBounds = false;
        options.inSampleSize = nSampleSize;
        options.inDither = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options.inPurgeable = true;

        Bitmap photo = BitmapFactory.decodeFile(strImagePath, options);
        if(checkOrientation
                && (degree==90 || degree==270) ){
            return getRotatedBitmap(photo, degree);
        }
        return photo;
    }



    public static Bitmap decodeImageFile(String strImagePath, BitmapFactory.Options options, boolean checkOrientation){
        if(checkOrientation==false){
            return BitmapFactory.decodeFile(strImagePath, options);
        }
        else{
            Bitmap bm = BitmapFactory.decodeFile(strImagePath, options);
            int degree = getExifDegree(strImagePath);
            return getRotatedBitmap(bm, degree);
        }
    }

    public static BitmapFactory.Options getBitmapSize(String strImagePath)
    {
        //==========================================
        // Loaded the temporary bitmap for getting size.
        //==========================================
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        //Bitmap photo = BitmapFactory.decodeFile(strPath, options);
        BitmapFactory.decodeFile(strImagePath, options);

        return options;
    }

    public static BitmapFactory.Options getBitmapSize(String strImagePath, boolean checkOrientation)
    {
        //==========================================
        // Loaded the temporary bitmap for getting size.
        //==========================================
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        //Bitmap photo = BitmapFactory.decodeFile(strPath, options);
        BitmapFactory.decodeFile(strImagePath, options);

        if(checkOrientation){
            int degree = getExifDegree(strImagePath);
            if(degree==90 || degree==270){
                int temp = options.outWidth;
                options.outWidth = options.outHeight;
                options.outHeight = temp;
            }
        }

        return options;
    }

    /****************************************************************************************************************
     * Get the sampling size for load the bitmap. (If you load the bitmap file of big size, This application is terminated.)
     * Parameter :
     *  - nOrgWidth	: The width of the original image (Value of outWidth of BitmapFactory.Options)
     *  - nOrgHeight : The height of the original image  (Value of outHeight of BitmapFactory.Options)
     *  - nMaxWidth : The width of the image of maximum size.  (width under 3M. ex.3000)
     *  - nMaxHeight : The height of the image of maximum size.   (height under 3M. ex.1000)	 *
     * Return :
     *  - Sampling size (If no need to resize, return 1). Throttled much larger.
     *  - If more than x.5 times , divide x+1 times.
     */
    public static int getSafeResizingSampleSize(
            int nOrgWidth,
            int nOrgHeight,
            int nMaxWidth,
            int nMaxHeight)
    {
        int size = 1;
        float fsize;
        float fWidthScale = 0;
        float fHeightScale = 0;

        if(nOrgWidth > nMaxWidth  || nOrgHeight > nMaxHeight )
        {
            if(nOrgWidth > nMaxWidth)
                fWidthScale = (float)nOrgWidth / (float)nMaxWidth;
            if(nOrgHeight > nMaxHeight)
                fHeightScale = (float)nOrgHeight / (float)nMaxHeight;

            if(fWidthScale >= fHeightScale) fsize = fWidthScale;
            else fsize= fHeightScale;

            size = (int)fsize;
        }

        return size;
    }

    public static int getExifDegree(String filepath){
        int degree = 0;
        ExifInterface exif;
        try {
            exif = new ExifInterface(filepath);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return 0;
        }


        if (exif != null){
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);

            if (orientation != -1){
                switch(orientation)	            {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        degree = 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        degree = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        degree = 270;
                        break;
                }
            }
        }
        return degree;
    }

    public static Bitmap getRotatedBitmap(Bitmap bitmap, int degrees)	{
        if ( degrees != 0 && bitmap != null ) {
            Matrix m = new Matrix();
            m.setRotate(degrees, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2 );
            try {
                Bitmap b2 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
                if (bitmap.equals(b2)){
                    bitmap.recycle();
                    bitmap = b2;
                }
            }
            catch (OutOfMemoryError ex){
                // TODO Auto-generated catch block
                ex.printStackTrace();
            }
        }
        return bitmap;
    }



    public static HashMap<String, Integer> getSettingLayoutLocaleResourceMap(boolean bUsePenSetting, boolean bUseEraserSetting, boolean bUseTextSetting, boolean bUseFillingSetting){
        //----------------------------------------
        // Resource Map for Layout & Locale
        //----------------------------------------
        HashMap<String,Integer> settingResourceMapInt = new HashMap<String, Integer>();
        // Layout
        if(bUsePenSetting){
            settingResourceMapInt.put(SCanvasConstants.LAYOUT_PEN_SPINNER, R.layout.mspinner);
        }
        //	if(bUseEraserSetting){
        //
        //	}
        if(bUseTextSetting){
            settingResourceMapInt.put(SCanvasConstants.LAYOUT_TEXT_SPINNER, R.layout.mspinnertext);
            settingResourceMapInt.put(SCanvasConstants.LAYOUT_TEXT_SPINNER_TABLET, R.layout.mspinnertext_tablet);
        }
        //	if(bUseFillingSetting){
        //
        //	}

        //----------------------------------------
        // Locale(Multi-Language Support)
        //----------------------------------------
        if(bUsePenSetting){
            settingResourceMapInt.put(SCanvasConstants.LOCALE_PEN_SETTING_TITLE, R.string.pen_settings);
            settingResourceMapInt.put(SCanvasConstants.LOCALE_PEN_SETTING_PRESET_EMPTY_MESSAGE, R.string.pen_settings_preset_empty);
            settingResourceMapInt.put(SCanvasConstants.LOCALE_PEN_SETTING_PRESET_DELETE_TITLE, R.string.pen_settings_preset_delete_title);
            settingResourceMapInt.put(SCanvasConstants.LOCALE_PEN_SETTING_PRESET_DELETE_MESSAGE, R.string.pen_settings_preset_delete_msg);
            settingResourceMapInt.put(SCanvasConstants.LOCALE_PEN_SETTING_PRESET_EXIST_MESSAGE, R.string.pen_settings_preset_exist);
            settingResourceMapInt.put(SCanvasConstants.LOCALE_PEN_SETTING_PRESET_MAXIMUM_MESSAGE, R.string.pen_settings_preset_maximum_msg);

            settingResourceMapInt.put(SCanvasConstants.LOCALE_PEN_SETTING_CHINESE_BRUSH_TAB, R.string.pen_settings_chinese_brush_tab);
            settingResourceMapInt.put(SCanvasConstants.LOCALE_PEN_SETTING_BEAUTIFY_BRUSH_TAB, R.string.pen_settings_beautify_brush_tab);
            settingResourceMapInt.put(SCanvasConstants.LOCALE_PEN_SETTING_BEAUTIFY_BRUSH_RESET, R.string.pen_settings_beautify_brush_reset);
            settingResourceMapInt.put(SCanvasConstants.LOCALE_PEN_SETTING_BEAUTIFY_BRUSH_CURSIVE, R.string.pen_settings_beautify_cursive);
            settingResourceMapInt.put(SCanvasConstants.LOCALE_PEN_SETTING_BEAUTIFY_BRUSH_SUSTENANCE, R.string.pen_settings_beautify_sustenance);
            settingResourceMapInt.put(SCanvasConstants.LOCALE_PEN_SETTING_BEAUTIFY_BRUSH_DUMMY, R.string.pen_settings_beautify_dummy);
            settingResourceMapInt.put(SCanvasConstants.LOCALE_PEN_SETTING_BEAUTIFY_BRUSH_MODULATION, R.string.pen_settings_beautify_modulation);
        }
        if(bUseEraserSetting){
            settingResourceMapInt.put(SCanvasConstants.LOCALE_ERASER_SETTING_TITLE, R.string.eraser_settings);
            settingResourceMapInt.put(SCanvasConstants.LOCALE_ERASER_SETTING_CLEARALL, R.string.clear_all);
        }
        if(bUseTextSetting){
            settingResourceMapInt.put(SCanvasConstants.LOCALE_TEXT_SETTING_TITLE, R.string.text_settings);
            settingResourceMapInt.put(SCanvasConstants.LOCALE_TEXT_SETTING_TAB_FONT, R.string.text_settings_tab_font);
            settingResourceMapInt.put(SCanvasConstants.LOCALE_TEXT_SETTING_TAB_PARAGRAPH, R.string.text_settings_tab_paragraph);
            settingResourceMapInt.put(SCanvasConstants.LOCALE_TEXT_SETTING_TAB_PARAGRAPH_ALIGN, R.string.text_settings_tab_paragraph_align);
            settingResourceMapInt.put(SCanvasConstants.LOCALE_TEXT_SETTING_TAB_LIST, R.string.text_settings_tab_list);
            settingResourceMapInt.put(SCanvasConstants.LOCALE_TEXTBOX_HINT, R.string.textbox_hint);

            settingResourceMapInt.put(SCanvasConstants.LOCALE_TEXT_SETTING_ALIGN_LEFT, R.string.align_left_desc);
            settingResourceMapInt.put(SCanvasConstants.LOCALE_TEXT_SETTING_ALIGN_CENTER, R.string.align_center_desc);
            settingResourceMapInt.put(SCanvasConstants.LOCALE_TEXT_SETTING_ALIGN_RIGHT, R.string.align_right_desc);

            settingResourceMapInt.put(SCanvasConstants.LOCALE_USER_FONT_NAME1, R.string.user_font_name1);
            settingResourceMapInt.put(SCanvasConstants.LOCALE_USER_FONT_NAME2, R.string.user_font_name2);
        }
        if(bUseFillingSetting){
            settingResourceMapInt.put(SCanvasConstants.LOCALE_FILLING_SETTING_TITLE, R.string.filling_settings);
        }
        // common
        settingResourceMapInt.put(SCanvasConstants.LOCALE_SETTINGVIEW_CLOSE_DESCRIPTION, R.string.settingview_close_btn_desc);
        settingResourceMapInt.put(SCanvasConstants.LOCALE_SETTINGVIEW_PRESET_ADD_DESCRIPTION, R.string.settingview_preset_add_btn_desc);
        settingResourceMapInt.put(SCanvasConstants.LOCALE_SETTINGVIEW_PRESET_DELETE_DESCRIPTION, R.string.settingview_preset_delete_btn_desc);

        return settingResourceMapInt;
    }

    public static HashMap<String, String> getSettingLayoutStringResourceMap(boolean bUsePenSetting, boolean bUseEraserSetting, boolean bUseTextSetting, boolean bUseFillingSetting){
        HashMap<String,String> settingResourceMapString = new HashMap<String, String>();
        if(bUseTextSetting){
            // Resource Map for Custom font path
            settingResourceMapString = new HashMap<String, String>();
            settingResourceMapString.put(SCanvasConstants.USER_FONT_PATH1, "fonts/chococooky.ttf");
            settingResourceMapString.put(SCanvasConstants.USER_FONT_PATH2, "fonts/rosemary.ttf");
        }

        // Set S Pen SDK Resource from Asset
        //settingResourceMapString.put(SCanvasConstants.CUSTOM_RESOURCE_ASSETS_PATH, "spen_sdk_resource");	// set folder of assets/spen_sdk_resource

        return settingResourceMapString;
    }

    public static void addTalkbackAndDescriptionStringResourceMap(HashMap<String, Integer> haspMap){
        haspMap.put(SCanvasConstants.TALKBACK_SETTING_CUSTOM_COLOR, R.string.custom_color_desc);
        haspMap.put(SCanvasConstants.TALKBACK_SETTING_DEFINED_COLOR, R.string.defined_color_desc);
        haspMap.put(SCanvasConstants.TALKBACK_SETTING_PEN_PREVIEW, R.string.pen_preview_desc);
        haspMap.put(SCanvasConstants.TALKBACK_SETTING_PENTYPE_SOLID, R.string.pentype_desc_solid);
        haspMap.put(SCanvasConstants.TALKBACK_SETTING_PENTYPE_BRUSH, R.string.pentype_desc_brush);
        haspMap.put(SCanvasConstants.TALKBACK_SETTING_PENTYPE_CHINESE_BRUSH, R.string.pentype_desc_chinese_brush);
        haspMap.put(SCanvasConstants.TALKBACK_SETTING_PENTYPE_CRAYON, R.string.pentype_desc_crayon);
        haspMap.put(SCanvasConstants.TALKBACK_SETTING_PENTYPE_MARKER, R.string.pentype_desc_marker);
        haspMap.put(SCanvasConstants.TALKBACK_SETTING_PRESET_PREVIEW, R.string.preset_preview_desc);
        haspMap.put(SCanvasConstants.TALKBACK_SETTING_SETTINGVIEW_CONTRACT, R.string.settingview_contract_desc);
        haspMap.put(SCanvasConstants.TALKBACK_SETTING_SETTINGVIEW_EXPAND, R.string.settingview_expand_desc);
        haspMap.put(SCanvasConstants.TALKBACK_SETTING_SETTINGVIEW_SCROLLBAR, R.string.settingview_scrollbar_desc);
        haspMap.put(SCanvasConstants.TALKBACK_SETTING_TEXT_FONT, R.string.text_font_desc);
        haspMap.put(SCanvasConstants.TALKBACK_SETTING_TEXT_PREVIEW, R.string.text_preview_desc);
        haspMap.put(SCanvasConstants.TALKBACK_SETTING_TEXT_SIZE, R.string.text_size_desc);
        haspMap.put(SCanvasConstants.TALKBACK_SETTING_SETTINGVIEW_COLORPICKERVIEW_COLOR, R.string.dropperview_color_desc);
        haspMap.put(SCanvasConstants.TALKBACK_SETTING_SETTINGVIEW_SEEKBAR_SIZE, R.string.seekbar_size_desc);
        haspMap.put(SCanvasConstants.TALKBACK_SETTING_SETTINGVIEW_SEEKBAR_OPACITY, R.string.seekbar_opacity_desc);
        haspMap.put(SCanvasConstants.TALKBACK_SETTING_TEXTBOX_DELETE, R.string.textbox_delete_desc);
        haspMap.put(SCanvasConstants.TALKBACK_SETTING_OBJECT_DELETE, R.string.object_delete_desc);
        haspMap.put(SCanvasConstants.TALKBACK_SETTING_OBJECT_ROTATE_LEFT, R.string.object_rotate_left_desc);
        haspMap.put(SCanvasConstants.TALKBACK_SETTING_OBJECT_ROTATE_RIGHT, R.string.object_rotate_right_desc);
        haspMap.put(SCanvasConstants.TALKBACK_SETTING_SEEKBAR_CURSIVE, R.string.seekbar_cursive_desc);
        haspMap.put(SCanvasConstants.TALKBACK_SETTING_SEEKBAR_SUSTENANCE, R.string.seekbar_sustenance_desc);
        haspMap.put(SCanvasConstants.TALKBACK_SETTING_SEEKBAR_DUMMY, R.string.seekbar_dummy_desc);
        haspMap.put(SCanvasConstants.TALKBACK_SETTING_SEEKBAR_MODULATION, R.string.seekbar_modulation_desc);
        haspMap.put(SCanvasConstants.TALKBACK_SETTING_BEAUTIFY_STYLE, R.string.beautify_style_desc);
        haspMap.put(SCanvasConstants.TALKBACK_SETTING_LIST_TYPE, R.string.list_type_desc);

        haspMap.put(SCanvasConstants.TALKBACK_SETTING_TYPE_BOLD_OFF, R.string.type_bold_off_desc);
        haspMap.put(SCanvasConstants.TALKBACK_SETTING_TYPE_BOLD_ON, R.string.type_bold_on_desc);
        haspMap.put(SCanvasConstants.TALKBACK_SETTING_TYPE_ITALIC_OFF, R.string.type_italic_off_desc);
        haspMap.put(SCanvasConstants.TALKBACK_SETTING_TYPE_ITALIC_ON, R.string.type_italic_on_desc);
        haspMap.put(SCanvasConstants.TALKBACK_SETTING_TYPE_UNDERLINE_OFF, R.string.type_underline_off_desc);
        haspMap.put(SCanvasConstants.TALKBACK_SETTING_TYPE_UNDERLINE_ON, R.string.type_underline_on_desc);
    }

    public static void alertActivityFinish(final Activity activity, String msg){
        AlertDialog.Builder ad = new AlertDialog.Builder(activity);
        ad.setIcon(activity.getResources().getDrawable(android.R.drawable.ic_dialog_alert));	// Android Resource
        ad.setTitle(activity.getResources().getString(R.string.app_name))
                .setMessage(msg)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // finish dialog
                        dialog.dismiss();
                        activity.finish();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
        ad = null;
    }

    // Update UI Button State
    public static void updateModeState(SCanvasView scanvasView, ImageView moveBtn, ImageView pointerBtn, ImageView penBtn, ImageView eraserBtn, ImageView textBtn, ImageView fillingBtn, ImageView insertBtn, ImageView colorPickerBtn, ImageView playBtn){
        boolean bMovingMode = scanvasView.isMovingMode();
        boolean bPointerMode = false;
        boolean bColorPickerMode = scanvasView.isColorPickerMode();
        int nCurMode = scanvasView.getCanvasMode();
        //		boolean bMovingModeEnableFlag = bMovingMode;	// flag to disable buttons in case of moving mode
        boolean bMovingModeEnableFlag = false;
        //----------------------------
        // Update Selection
        //----------------------------
        if(moveBtn!=null) moveBtn.setSelected(bMovingMode);
        if(pointerBtn!=null) pointerBtn.setSelected(bPointerMode);
        if(penBtn!=null) penBtn.setSelected(bMovingMode? false : nCurMode==SCanvasConstants.SCANVAS_MODE_INPUT_PEN);
        if(eraserBtn!=null) eraserBtn.setSelected(bMovingMode? false : nCurMode==SCanvasConstants.SCANVAS_MODE_INPUT_ERASER);
        if(textBtn!=null) textBtn.setSelected(bMovingMode? false : nCurMode==SCanvasConstants.SCANVAS_MODE_INPUT_TEXT);
        if(fillingBtn!=null) fillingBtn.setSelected(bMovingMode? false : nCurMode==SCanvasConstants.SCANVAS_MODE_INPUT_FILLING);
        if(colorPickerBtn!=null) colorPickerBtn.setSelected(bMovingMode? false : bColorPickerMode);

        //----------------------------
        // Update Enable/disable
        //----------------------------
        if(pointerBtn!=null) pointerBtn.setEnabled(!bMovingModeEnableFlag);
        if(penBtn!=null) penBtn.setEnabled(!(bMovingModeEnableFlag ));
        if(eraserBtn!=null) eraserBtn.setEnabled(!(bMovingModeEnableFlag || bColorPickerMode));
        if(textBtn!=null) textBtn.setEnabled(!(bMovingModeEnableFlag ));
        if(fillingBtn!=null) fillingBtn.setEnabled(!(bMovingModeEnableFlag));
        if(insertBtn!=null) insertBtn.setEnabled(!(bMovingModeEnableFlag ));
        if(colorPickerBtn!=null) colorPickerBtn.setEnabled(!(bMovingModeEnableFlag || nCurMode==SCanvasConstants.SCANVAS_MODE_INPUT_ERASER));

        boolean bVideoViewExist = scanvasView.isVideoViewExist();
        if(pointerBtn!=null) pointerBtn.setEnabled(!bVideoViewExist);
        if(penBtn!=null) penBtn.setEnabled(!bVideoViewExist);
        if(eraserBtn!=null) eraserBtn.setEnabled(!bVideoViewExist);
        if(textBtn!=null) textBtn.setEnabled(!bVideoViewExist);
        if(fillingBtn!=null) fillingBtn.setEnabled(!bVideoViewExist);
        if(insertBtn!=null) insertBtn.setEnabled(!bVideoViewExist);
        if(colorPickerBtn!=null) colorPickerBtn.setEnabled(!bVideoViewExist);
        if(playBtn!=null) playBtn.setEnabled(!bVideoViewExist);
    }

    public static void updateModeState(SCanvasView scanvasView, ImageView moveBtn, ImageView pointerBtn, ImageView penBtn, ImageView eraserBtn, ImageView textBtn, ImageView fillingBtn, ImageView insertBtn, ImageView colorPickerBtn, ImageView playBtn, ImageView selectionModeBtn){
        boolean bMovingMode = scanvasView.isMovingMode();
        boolean bPointerMode = false;
        boolean bColorPickerMode = scanvasView.isColorPickerMode();
        int nCurMode = scanvasView.getCanvasMode();
        boolean bMovingModeEnableFlag = false;
        //----------------------------
        // Update Selection
        //----------------------------
        if(selectionModeBtn != null) selectionModeBtn.setSelected(bMovingMode? false : nCurMode==SCanvasConstants.SCANVAS_MODE_SELECT);
        if(moveBtn!=null) moveBtn.setSelected(bMovingMode);
        if(pointerBtn!=null) pointerBtn.setSelected(bPointerMode);
        if(penBtn!=null) penBtn.setSelected(bMovingMode? false : nCurMode==SCanvasConstants.SCANVAS_MODE_INPUT_PEN);
        if(eraserBtn!=null) eraserBtn.setSelected(bMovingMode? false : nCurMode==SCanvasConstants.SCANVAS_MODE_INPUT_ERASER);
        if(textBtn!=null) textBtn.setSelected(bMovingMode? false : nCurMode==SCanvasConstants.SCANVAS_MODE_INPUT_TEXT);
        if(fillingBtn!=null) fillingBtn.setSelected(bMovingMode? false : nCurMode==SCanvasConstants.SCANVAS_MODE_INPUT_FILLING);
        if(colorPickerBtn!=null) colorPickerBtn.setSelected(bMovingMode? false : bColorPickerMode);

        //----------------------------
        // Update Enable/disable
        //----------------------------
        if(pointerBtn!=null) pointerBtn.setEnabled(!bMovingModeEnableFlag);
        if(penBtn!=null) penBtn.setEnabled(!(bMovingModeEnableFlag ));
        if(eraserBtn!=null) eraserBtn.setEnabled(!(bMovingModeEnableFlag || bColorPickerMode));
        if(textBtn!=null) textBtn.setEnabled(!(bMovingModeEnableFlag ));
        if(fillingBtn!=null) fillingBtn.setEnabled(!(bMovingModeEnableFlag));
        if(insertBtn!=null) insertBtn.setEnabled(!(bMovingModeEnableFlag ));
        if(colorPickerBtn!=null) colorPickerBtn.setEnabled(!(bMovingModeEnableFlag || nCurMode==SCanvasConstants.SCANVAS_MODE_INPUT_ERASER));

        boolean bVideoViewExist = scanvasView.isVideoViewExist();
        if(pointerBtn!=null) pointerBtn.setEnabled(!bVideoViewExist);
        if(penBtn!=null) penBtn.setEnabled(!bVideoViewExist);
        if(eraserBtn!=null) eraserBtn.setEnabled(!bVideoViewExist);
        if(textBtn!=null) textBtn.setEnabled(!bVideoViewExist);
        if(fillingBtn!=null) fillingBtn.setEnabled(!bVideoViewExist);
        if(insertBtn!=null) insertBtn.setEnabled(!bVideoViewExist);
        if(colorPickerBtn!=null) colorPickerBtn.setEnabled(!bVideoViewExist);
        if(playBtn!=null) playBtn.setEnabled(!bVideoViewExist);
    }
}
