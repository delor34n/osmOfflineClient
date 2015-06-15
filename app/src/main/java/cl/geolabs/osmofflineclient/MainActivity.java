package cl.geolabs.osmofflineclient;

import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.mapsforge.core.model.LatLong;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.util.AndroidUtil;
import org.mapsforge.map.android.view.MapView;
import org.mapsforge.map.layer.cache.TileCache;
import org.mapsforge.map.layer.renderer.TileRendererLayer;
import org.mapsforge.map.rendertheme.InternalRenderTheme;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;

public class MainActivity extends ActionBarActivity {

    private MapView mMapview;
    private TileCache mTileCache;
    private TileRendererLayer mTileRendererLayer;

    private static String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidGraphicFactory.createInstance(getApplication());
        setContentView(R.layout.activity_main);

        //Get the map view.
        mMapview = (MapView) findViewById(R.id.mapView);
        mMapview.setLayerType(View.LAYER_TYPE_HARDWARE, null);

        mMapview.setClickable(true);

        mTileCache = AndroidUtil.createTileCache(this, "mapcache",
                mMapview.getModel().displayModel.getTileSize(),
                1f,
                mMapview.getModel().frameBufferModel.getOverdrawFactor());

        mMapview.getModel().mapViewPosition.setZoomLevel((byte) 15);

        String dirPath = Environment.getExternalStorageDirectory().getPath() + "/osmOfflineClient/";
        File dir = new File(dirPath);
        if(!dir.exists())
            dir.mkdir();

        // tile renderer layer using internal render theme
        mTileRendererLayer = new TileRendererLayer(mTileCache,
                mMapview.getModel().mapViewPosition, false, AndroidGraphicFactory.INSTANCE);
        try{
            mTileRendererLayer.setMapFile(dir.listFiles()[0]);
        }catch (Throwable e){
            Log.e(TAG, "Error creating map: "+e);
        }

        mTileRendererLayer.setXmlRenderTheme(InternalRenderTheme.OSMARENDER);

        // only once a layer is associated with a mapView the rendering starts
        mMapview.getLayerManager().getLayers().add(mTileRendererLayer);

        //mapView.setClickable(true);
        mMapview.setBuiltInZoomControls(true);
        mMapview.getMapScaleBar().setVisible(false);

        mMapview.getModel().mapViewPosition.setCenter(new LatLong(-33.5394144, -70.5610768));

        /*MyMarker marker = new MyMarker(this, new LatLong(43.385833, -8.406389), AndroidGraphicFactory.convertToBitmap(getResources().getDrawable(R.drawable.ic_launcher)), 0, 0);
        mapView.getLayerManager().getLayers().add(marker);*/

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
