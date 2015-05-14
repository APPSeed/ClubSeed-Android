package cn.edu.ustc.appseed.clubseed.activity;

/**
 * Created by shenaolin on 15/4/15.
 */
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import cn.edu.ustc.appseed.clubseed.R;
import cn.edu.ustc.appseed.clubseed.data.Event;
import cn.edu.ustc.appseed.clubseed.data.ViewActivityPhp;
import cn.edu.ustc.appseed.clubseed.utils.AppUtils;
import android.util.FloatMath;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by J.H. on 2015/4/2.
 */
public class GraphOnlyActivity extends Activity implements View.OnTouchListener{
    private final int TOUCH_CONST=10;
    private long touchTime=0;
    private int touched=0;
    private long clickTime=0;
    private boolean clicked=false;

    private UpdateThread thread;
    private Graph graph;
    private Bitmap bitmap;
    private int ID;
    private int pointerCount=0;
    private Vector2 point;
    private float originDistance=1;
    private float currentDistance=1;
    private float originScale;
    private float fscale;
    private float maxScale;
    private float minScale;
    private float scale=1;
    private Vector2 center;
    private float width;
    private float height;
    private float screenWidth;
    private float screenHeight;
    private boolean loose=true;
    private boolean fit=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        graph=new Graph(this);
        setContentView(graph);
        ID = getIntent().getIntExtra(EventContentActivity.EXTRA_BITMAP, 0);
        bitmap=AppUtils.graphs.get(ID);
        if(!AppUtils.graphs.containsKey(ID)||bitmap==null){
            onBackPressed();
        }
        DisplayMetrics dm=new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenWidth=dm.widthPixels;
        screenHeight=dm.heightPixels;
        width=bitmap.getWidth();
        height=bitmap.getHeight();

        center=new Vector2(screenWidth/2,screenHeight/2);
        scale=Math.min(screenWidth/width,screenHeight/height);
        originScale=scale;
        maxScale=scale*5;
        minScale=scale/2;
        fscale=scale;
        point=new Vector2();
        thread=new UpdateThread();
        graph.setOnTouchListener(this);
        registerForContextMenu(graph);
        thread.start();
    }
    /*@Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuItem save;
        MenuItem send;
        save=menu.add(0,1,0,"保存");
        send=menu.add(0,2,0,"发送");
        save.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                System.out.println("save");
                saveGraph();
                return true;
            }
        });
        send.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return true;
            }
        });
        return true;
    }*/
    /*private void saveGraph(){
        File file=new File("aa","a.png");
        try{
            FileOutputStream out=new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG,90,out);
            out.flush();
            out.close();
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }
    }*/
    private void back(){
        System.out.println("back");
        touched=0;
        clicked=false;
        Runtime runtime=Runtime.getRuntime();
        try {
            runtime.exec("input keyevent "+ KeyEvent.KEYCODE_BACK);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    private void onClicked(){
        System.out.println("click");
        if(clicked&&System.currentTimeMillis()-clickTime<200){
            System.out.println("click2");
            clicked=false;
            center.set(screenWidth/2,screenHeight/2);
            scale=fscale;
        }else{
            System.out.println("click1");
            clicked=true;
            clickTime=System.currentTimeMillis();
        }
        touched=0;
    }
    private void onLongClicked(){
        System.out.println("long");
        touched=0;
        clicked=false;
        Runtime runtime=Runtime.getRuntime();
        try {
            runtime.exec("input keyevent "+ KeyEvent.KEYCODE_MENU);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public boolean onTouch(View v,MotionEvent event){
        Vector2 []vectors;
        float distance;
        float X=0,Y=0;
        int action=event.getAction()&MotionEvent.ACTION_MASK;
        pointerCount=event.getPointerCount();
        vectors=new Vector2[pointerCount];
        X=0;
        Y=0;
        for(int i=0;i<pointerCount;++i){
            vectors[i]=new Vector2(event.getX(i),event.getY(i));
            X+=event.getX(i);
            Y+=event.getY(i);
        }
        distance=getDistance(vectors);
        switch(action){
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                if(pointerCount==1){
                    touched=TOUCH_CONST;
                    touchTime=System.currentTimeMillis();
                }else if(pointerCount>=2){
                    touched=0;
                    clicked=false;
                }
                point.set(X/pointerCount,Y/pointerCount);
                originDistance=distance;
                originScale=scale;
                loose=false;
                break;
            case MotionEvent.ACTION_MOVE:
                if(pointerCount==1&&touched>0){
                    --touched;
                }else if(pointerCount>=2){
                    touched=0;
                    clicked=false;
                }
                if(fit){
                    point.set(X/pointerCount,Y/pointerCount);
                    originDistance=distance;
                    originScale=scale;
                    fit=false;
                }
                currentDistance=distance;
                if(pointerCount>=2){
                    scale=originScale*currentDistance/originDistance;
                    if(scale<minScale) scale=minScale;
                    if(scale>maxScale) scale=maxScale;
                }
                if(scale>=fscale){
                    if(center.x+X/pointerCount-point.x-width*scale/2<=0&&center.x+X/pointerCount-point.x+width*scale/2>=screenWidth){
                        center.add(X/pointerCount-point.x,0);
                    }
                    if(center.y+Y/pointerCount-point.y-height*scale/2<=0&&center.y+Y/pointerCount-point.y+height*scale/2>=screenHeight){
                        center.add(0,Y/pointerCount-point.y);
                    }
                }else{
                    if((screenWidth/2-center.x)*(X/pointerCount-point.x)>0&&(screenHeight/2-center.y)*(Y/pointerCount-point.y)>0){
                        center.add(X/pointerCount-point.x,Y/pointerCount-point.y);
                    }
                }
                if(scale*width>=screenWidth){
                    if(center.x-width*scale/2>0) center.set(width*scale/2,center.y);
                    if(center.x+width*scale/2<screenWidth) center.set(screenWidth-width*scale/2,center.y);
                }
                if(scale*height>screenHeight){
                    if(center.y-height*scale/2>0) center.set(center.x,height*scale/2);
                    if(center.y+height*scale/2<screenHeight) center.set(center.x,screenHeight-height*scale/2);
                }
                point.set(X/pointerCount,Y/pointerCount);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_CANCEL:
                if(pointerCount==1&&touched!=0&&System.currentTimeMillis()-touchTime<=200){
                    onClicked();
                }else if(pointerCount>=2){
                    touched=0;
                    clicked=false;
                }
                originScale=scale;
                fit=true;
                loose=true;
                break;
        }
        return true;
    }

    private float getDistance(Vector2...vectors){
        float sum=0;
        for(int i=0;i<vectors.length-1;++i){
            for(int j=i+1;j<vectors.length;++j){
                sum+=vectors[i].dist(vectors[j]);
            }
        }
        return sum;
    }
    private class UpdateThread extends Thread{
        @Override
        public void run(){
            while(true){
                if(clicked&&System.currentTimeMillis()-clickTime>200) back();
                if(touched>0&&System.currentTimeMillis()-touchTime>500) onLongClicked();
                graph.postInvalidate();
                try{
                    Thread.sleep(30);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
    private class Graph extends View{
        public Graph(Context context){
            super(context);
        }
        @Override
        protected void onDraw(Canvas canvas){
            if(loose&&scale<=fscale){
                scale+=0.05;
                if(scale>fscale) scale=fscale;
                Vector2 diff=center.cpy().sub(screenWidth/2,screenHeight/2);
                center.add(diff.cpy().nor().mul(-screenWidth/10));
                if(center.cpy().sub(screenWidth/2,screenHeight/2).mul(diff)<0){
                    center.set(screenWidth/2,screenHeight/2);
                }
            }
            RectF dst=new RectF();
            dst.set(center.x-width*scale/2,center.y-height*scale/2,center.x+width*scale/2,center.y+height*scale/2);
            canvas.drawBitmap(bitmap,new Rect(0,0,(int)width,(int)height),dst,null);
        }
    }
}
class Vector2{
    public final static float TO_RADIANS=(1/180.0f)*(float)Math.PI;
    public final static float TO_DEGREES=(1/(float)Math.PI)*180;
    public float x,y;
    public Vector2(){}
    public Vector2(float x,float y){
        this.x=x;
        this.y=y;
    }
    public Vector2(Vector2 other){
        this.x=other.x;
        this.y=other.y;
    }
    public Vector2 cpy(){
        return new Vector2(x,y);
    }
    public Vector2 set(float x,float y){
        this.x=x;
        this.y=y;
        return this;
    }
    public Vector2 set(Vector2 other){
        this.x=other.x;
        this.y=other.y;
        return this;
    }
    public Vector2 add(float x,float y){
        this.x+=x;
        this.y+=y;
        return this;
    }
    public Vector2 add(Vector2 other){
        this.x+=other.x;
        this.y+=other.y;
        return this;
    }
    public Vector2 sub(float x,float y){
        this.x-=x;
        this.y-=y;
        return this;
    }
    public Vector2 sub(Vector2 other){
        this.x-=other.x;
        this.y-=other.y;
        return this;
    }
    public Vector2 mul(float scalar){
        this.x*=scalar;
        this.y*=scalar;
        return this;
    }
    public float mul(Vector2 other){
        return x*other.x+y*other.y;
    }
    public float mul(float x,float y){
        return this.x*x+this.y*y;
    }
    public float len(){
        return FloatMath.sqrt(x*x+y*y);
    }
    public Vector2 nor(){
        float len=len();
        if(len!=0){
            this.x/=len;
            this.y/=len;
        }
        return this;
    }
    public float angle(){
        float angle=(float)Math.atan2(y, x)*TO_DEGREES;
        while(angle<0){
            angle+=360;
        }
        return angle;
    }
    public Vector2 rotate(float angle){
        float rad=angle*TO_RADIANS;
        float cos=FloatMath.cos(rad);
        float sin=FloatMath.sin(rad);
        float newX=this.x*cos-this.y*sin;
        float newY=this.x*sin+this.y*cos;
        return set(newX,newY);
    }
    public float dist(Vector2 other){
        float distX=this.x-other.x;
        float distY=this.y-other.y;
        return FloatMath.sqrt(distX*distX+distY*distY);
    }
    public float dist(float x,float y){
        float distX=this.x-x;
        float distY=this.y-y;
        return FloatMath.sqrt(distX*distX+distY*distY);
    }
    public float distSquared(Vector2 other){
        float distX=this.x-other.x;
        float distY=this.y-other.y;
        return distX*distX+distY*distY;
    }
    public float distSquared(float x,float y){
        float distX=this.x-x;
        float distY=this.y-y;
        return distX*distX+distY*distY;
    }
}

