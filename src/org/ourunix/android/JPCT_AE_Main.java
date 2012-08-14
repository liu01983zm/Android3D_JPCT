package org.ourunix.android;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.sax.Element;
import android.sax.EndElementListener;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.util.Xml;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;
/**
 * http://www.jpct.net/jpct-ae/doc/index.html
 * */
public class JPCT_AE_Main extends ListActivity {
	 private List<Map<String, Object>> mData;

	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);

	        mData = parseDemosXml();

	        SimpleAdapter adapter = new SimpleAdapter(this, mData, R.layout.item, new String[] {
	                "title", "description", "image" }, new int[] { R.id.item_title,
	                R.id.item_description, R.id.item_image });
	        setListAdapter(adapter);
	        getListView().setOnItemClickListener(new OnItemClickListener(){

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					System.out.println();
					System.out.println("----------------onItemClick--position="+position+"---------------------");
					System.out.println();
					Intent intent = new Intent();
			        Bundle bundle = new Bundle();
			        
			        // TODO
			        intent.setClass(JPCT_AE_Main.this, GLSurfaceViewActivity.class);
			        bundle.putInt("INDEX", position);
			        bundle.putString("TITLE", mData.get(position).get("title").toString());
			        intent.putExtras(bundle);
			        startActivity(intent);
					
				}});
	    }

	    private List<Map<String, Object>> parseDemosXml() {

	        final List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
	        final Map<String, Object> currentItem = new HashMap<String, Object>();

	        RootElement root = new RootElement("demos");
	        Element item = root.getChild("demo");
	        item.setEndElementListener(new EndElementListener() {
	            public void end() {
	                items.add(new HashMap<String, Object>(currentItem));
	            }
	        });
	        item.getChild("title").setEndTextElementListener(new EndTextElementListener() {
	            public void end(String value) {
	                currentItem.put("title", value);
	            }
	        });
	        item.getChild("description").setEndTextElementListener(new EndTextElementListener() {
	            public void end(String value) {
	                currentItem.put("description", value);
	            }
	        });
	        item.getChild("id").setEndTextElementListener(new EndTextElementListener() {
	            public void end(String value) {
	                currentItem.put("id", Integer.parseInt(value));
	            }
	        });
	        item.getChild("image").setEndTextElementListener(new EndTextElementListener() {
	            public void end(String value) {
	                int resId = getResources().getIdentifier(value, null, getPackageName());
	                currentItem.put("image", resId);
	            }
	        });

	        try {
	            Xml.parse(getResources().openRawResource(R.raw.index), Xml.Encoding.UTF_8, root
	                    .getContentHandler());
	        } catch (Exception e) {
	            throw new RuntimeException(e);
	        }

	        return items;
	    }

    
}