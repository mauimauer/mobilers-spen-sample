package de.mobilers.android.spensample.fragments;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.samsung.samm.common.SOptionSCanvas;
import com.samsung.spensdk.SCanvasConstants;
import com.samsung.spensdk.SCanvasView;
import com.samsung.spensdk.applistener.SCanvasInitializeListener;
import com.samsung.spensdk.applistener.SCanvasModeChangedListener;
import com.samsung.spensdk.example.tools.SPenSDKUtils;

import java.io.File;
import java.util.HashMap;

import de.mobilers.android.spensample.Consts;
import de.mobilers.android.spensample.R;
import de.mobilers.android.spensample.dummy.DummyContent;
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

/**
 * A fragment representing a single Drawing detail screen.
 * This fragment is either contained in a {@link de.mobilers.android.spensample.activity.DrawingListActivity}
 * in two-pane mode (on tablets) or a {@link de.mobilers.android.spensample.activity.DrawingDetailActivity}
 * on handsets.
 */
public class DrawingDetailFragment extends RoboFragment {

    @InjectView(R.id.canvas_container)
    private RelativeLayout mCanvasContainer;

    @InjectView(R.id.layout_container)
    private FrameLayout mLayoutContainer;

    private SCanvasView mSCanvas;
    private File mDrawingFile;

    public static final String TAG = "DrawingDetailFragment";

    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private DummyContent.DummyItem mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public DrawingDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = DummyContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));
        }
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.drawing_menu, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if(mSCanvas != null) {
            menu.findItem(R.id.menu_undo).setEnabled(mSCanvas.isUndoable());
            menu.findItem(R.id.menu_redo).setEnabled(mSCanvas.isRedoable());

            if(!mSCanvas.isFingerControlPenDrawing())
                menu.findItem(R.id.menu_pen_only).setIcon(R.drawable.selector_penonly_n);
            else
                menu.findItem(R.id.menu_pen_only).setIcon(R.drawable.selector_penonly);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        getActivity().invalidateOptionsMenu();
        int id = item.getItemId();
        switch(id) {
            case R.id.menu_undo:
                mSCanvas.undo();
                break;
            case R.id.menu_redo:
                mSCanvas.redo();
                break;
            case R.id.menu_colorPicker:
                boolean bIsColorPickerMode = !mSCanvas.isColorPickerMode();
                mSCanvas.setColorPickerMode(bIsColorPickerMode);
                break;
            case R.id.menu_pen_only:
                boolean bIsPenOnly = !mSCanvas.isFingerControlPenDrawing();
                mSCanvas.setFingerControlPenDrawing(bIsPenOnly);
                break;
            case R.id.menu_pen:
                if(mSCanvas.getCanvasMode()==SCanvasConstants.SCANVAS_MODE_INPUT_PEN){
                    mSCanvas.setSettingViewSizeOption(SCanvasConstants.SCANVAS_SETTINGVIEW_PEN, SCanvasConstants.SCANVAS_SETTINGVIEW_SIZE_EXT);
                    mSCanvas.toggleShowSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_PEN);
                }
                else{
                    mSCanvas.setCanvasMode(SCanvasConstants.SCANVAS_MODE_INPUT_PEN);
                    mSCanvas.showSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_PEN, false);
                    updateModeState();
                }
                break;
            case R.id.menu_erase:
                if(mSCanvas.getCanvasMode()==SCanvasConstants.SCANVAS_MODE_INPUT_ERASER){
                    mSCanvas.setSettingViewSizeOption(SCanvasConstants.SCANVAS_SETTINGVIEW_ERASER, SCanvasConstants.SCANVAS_SETTINGVIEW_SIZE_NORMAL);
                    mSCanvas.toggleShowSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_ERASER);
                }
                else {
                    mSCanvas.setCanvasMode(SCanvasConstants.SCANVAS_MODE_INPUT_ERASER);
                    mSCanvas.showSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_ERASER, false);
                    updateModeState();
                }
                break;
            case R.id.menu_text:
                if(mSCanvas.getCanvasMode()==SCanvasConstants.SCANVAS_MODE_INPUT_TEXT){
                    mSCanvas.setSettingViewSizeOption(SCanvasConstants.SCANVAS_SETTINGVIEW_TEXT, SCanvasConstants.SCANVAS_SETTINGVIEW_SIZE_NORMAL);
                    mSCanvas.toggleShowSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_TEXT);
                }
                else{
                    mSCanvas.setCanvasMode(SCanvasConstants.SCANVAS_MODE_INPUT_TEXT);
                    mSCanvas.showSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_TEXT, false);
                    updateModeState();
                    Toast.makeText(getActivity(), "Tap Canvas to insert Text", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.menu_filling:
                if(mSCanvas.getCanvasMode()==SCanvasConstants.SCANVAS_MODE_INPUT_FILLING){
                    mSCanvas.setSettingViewSizeOption(SCanvasConstants.SCANVAS_SETTINGVIEW_FILLING, SCanvasConstants.SCANVAS_SETTINGVIEW_SIZE_NORMAL);
                    mSCanvas.toggleShowSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_FILLING);
                }
                else{
                    mSCanvas.setCanvasMode(SCanvasConstants.SCANVAS_MODE_INPUT_FILLING);
                    mSCanvas.showSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_FILLING, false);
                    updateModeState();
                    Toast.makeText(getActivity(), "Tap Canvas to fill color", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_drawing_detail, container, false);

        // Show the dummy content as text in a TextView.
        if (mItem != null) {

        }

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated()");
        super.onActivityCreated(savedInstanceState);

        mSCanvas = new SCanvasView(getActivity());
        mSCanvas.addedByResizingContainer(mCanvasContainer);

        HashMap<String,Integer> settingResourceMapInt = SPenSDKUtils.getSettingLayoutLocaleResourceMap(true, true, true, true);
        SPenSDKUtils.addTalkbackAndDescriptionStringResourceMap(settingResourceMapInt);
        HashMap<String,String> settingResourceMapString = SPenSDKUtils.getSettingLayoutStringResourceMap(true, true, true, true);
        mSCanvas.createSettingView(mLayoutContainer, settingResourceMapInt, settingResourceMapString);

        mSCanvas.setSCanvasInitializeListener(new SCanvasInitializeListener() {
            @Override
            public void onInitialized() {
                //--------------------------------------------
                // Start SCanvasView/CanvasView Task Here
                //--------------------------------------------
                // Application Identifier Setting
                if(!mSCanvas.setAppID(Consts.APPLICATION_ID_NAME, Consts.APPLICATION_ID_VERSION_MAJOR, Consts.APPLICATION_ID_VERSION_MINOR, Consts.APPLICATION_ID_VERSION_PATCHNAME))
                    Toast.makeText(getActivity(), "Fail to set App ID.", Toast.LENGTH_LONG).show();

                // Set Title
                if(!mSCanvas.setTitle("mobilers S-Pen Sample"))
                    Toast.makeText(getActivity(), "Fail to set Title.", Toast.LENGTH_LONG).show();

                // Set Initial Setting View Size
                mSCanvas.setSettingViewSizeOption(SCanvasConstants.SCANVAS_SETTINGVIEW_PEN, SCanvasConstants.SCANVAS_SETTINGVIEW_SIZE_EXT);

                // Set Editor Version (mEditorGUIStyle)
                mSCanvas.setSCanvasGUIStyle(SCanvasConstants.SCANVAS_GUI_STYLE_NORMAL);

                // Set Pen Only Mode with Finger Control
                mSCanvas.setFingerControlPenDrawing(true);

                // Set Editor GUI Style (mbSingleSelectionFixedLayerMode)
                // - true :  S Pen SDK 2.2 (Single selection, Fixed layer Editor : Image-Text-Stroke ordering)
                // - false : S Pen SDK 2.3 (Multi-selection, Flexible layer Editor : Input ordering)
                mSCanvas.setSingleSelectionFixedLayerMode(false);

                // Update button state
                updateModeState();

                mDrawingFile = new File(getActivity().getDir("drawings", Context.MODE_PRIVATE).getAbsolutePath()+"/"+mItem.id+".png");

                // Load the file & set Background Image
                if(mDrawingFile.exists()){

                    if(SCanvasView.isSAMMFile(mDrawingFile.getAbsolutePath())){
                        loadSAMMFile(mDrawingFile.getAbsolutePath());
                        // Set the editing rect after loading
                    }
                }

            }
        });

        mSCanvas.setSCanvasModeChangedListener(new SCanvasModeChangedListener() {

            @Override
            public void onModeChanged(int mode) {
                updateModeState();
            }

            @Override
            public void onMovingModeEnabled(boolean bEnableMovingMode) {
                updateModeState();
            }

            @Override
            public void onColorPickerModeEnabled(boolean bEnableColorPickerMode) {
                updateModeState();
            }
        });

        mSCanvas.setSCanvasHoverPointerStyle(SCanvasConstants.SCANVAS_HOVERPOINTER_STYLE_SPENSDK);
    }

    @Override
    public void onStart() {
        super.onStart();


        getActivity().invalidateOptionsMenu();
    }

    // Load SAMM file
    boolean loadSAMMFile(String strFileName){
        Log.d(TAG, "loadSAMMFile()");
        if(mSCanvas.isAnimationMode()){
            // It must be not animation mode.
        }
        else {
            // set progress dialog
            mSCanvas.setProgressDialogSetting(R.string.load_title, R.string.load_msg, ProgressDialog.STYLE_HORIZONTAL, false);

            // canvas option setting
            SOptionSCanvas canvasOption = mSCanvas.getOption();
            if(canvasOption == null)
                return false;
           /* canvasOption.mSAMMOption.setConvertCanvasSizeOption(PreferencesOfSAMMOption.getPreferenceLoadCanvasSize(getActivity()));
            canvasOption.mSAMMOption.setConvertCanvasHorizontalAlignOption(PreferencesOfSAMMOption.getPreferenceLoadCanvasHAlign(getActivity()));
            canvasOption.mSAMMOption.setConvertCanvasVerticalAlignOption(PreferencesOfSAMMOption.getPreferenceLoadCanvasVAlign(getActivity()));
            canvasOption.mSAMMOption.setDecodePriorityFGData(PreferencesOfSAMMOption.getPreferenceDecodePriorityFGData(getActivity()));*/
            // option setting
            mSCanvas.setOption(canvasOption);

            // show progress for loading data
            if(mSCanvas.loadSAMMFile(strFileName, true, true, true)){
                // Loading Result can be get by callback function
            }
            else{
                Toast.makeText(getActivity(), "Load AMS File("+ strFileName +") Fail!", Toast.LENGTH_LONG).show();
                return false;
            }
        }
        return true;
    }

    private void updateModeState(){
        //SPenSDKUtils.updateModeState(mSCanvas, null, null, mPenBtn, mEraserBtn, mTextBtn, mFillingBtn, mInsertBtn, mColorPickerBtn, null);
    }

    private void callGalleryForInputImage(int nRequestCode){
        try {
            Intent galleryIntent;
            galleryIntent = new Intent();
            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");
            galleryIntent.setClassName("com.cooliris.media", "com.cooliris.media.Gallery");
            startActivityForResult(galleryIntent, nRequestCode);
        } catch(ActivityNotFoundException e) {
            Intent galleryIntent;
            galleryIntent = new Intent();
            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");
            startActivityForResult(galleryIntent, nRequestCode);
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause()");
        if(!mSCanvas.saveSAMMFile(mDrawingFile.getAbsolutePath())) {
            Toast.makeText(getActivity(),"Saving failed...", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        mSCanvas.closeSCanvasView();
    }
}
