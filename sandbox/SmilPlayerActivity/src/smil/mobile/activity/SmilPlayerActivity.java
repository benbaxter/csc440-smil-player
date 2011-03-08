package smil.mobile.activity;

//import java.io.File;
import java.util.List;


import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
//import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import smil.mobile.activity.SmilConstants;
import smil.mobile.activity.SMILAudioComponent;
import smil.mobile.activity.SMILComponent;
import smil.mobile.activity.SMILImageComponent;
import smil.mobile.activity.SMILMessage;
import smil.mobile.activity.SMILTextComponent;
//import smil.mobile.xml.parsing.SmilParser;


public class SmilPlayerActivity extends Activity
{
    private Handler mHandler = new Handler();


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        LinearLayout layout = (LinearLayout) this.findViewById(R.id.playerLayout);
        layout.setOrientation(LinearLayout.VERTICAL);

        System.out.println("got layout");
        try
        {
			String fileName = "example.smil"; //in.getStringExtra(getString(R.string.fileName));
			SMILMessage smilMessage = SMILReader.parseMessage(fileName);
            
            if (smilMessage != null) //(smilFile != null)
            {
                List<SMILComponent> componentList = smilMessage.getComponentList(); //value.getComponentList();

                for (SMILComponent component : componentList)
                {
                    switch (component.getType())
                    {
                        case SmilConstants.COMPONENT_TYPE_TEXT:
                            TextView textView = new TextView(getApplicationContext());  //(this);
                            textView.setText(((SMILTextComponent) component).getText());
                            layout.addView(textView);
                            mHandler.postDelayed(new RemoveTask(textView), component.getDuration() * 1000);
                            break;

                        case SmilConstants.COMPONENT_TYPE_AUDIO:
                            MediaPlayer audioPlayer = new MediaPlayer();
                            String audioPath = ((SMILAudioComponent) component).getSrc();
                            audioPlayer.setDataSource(audioPath);
                            audioPlayer.prepare();
                            audioPlayer.start();
                            mHandler.postDelayed(new RemoveAudio(audioPlayer), component.getDuration() * 1000);
                            break;
                        
                        case SmilConstants.COMPONENT_TYPE_VIDEO:
                            MediaPlayer videoPlayer = new MediaPlayer();
                            String videoPath = ((SMILAudioComponent) component).getSrc();
                            videoPlayer.setDataSource(videoPath);
                            videoPlayer.prepare();
                            videoPlayer.start();
                            mHandler.postDelayed(new RemoveAudio(videoPlayer), component.getDuration() * 1000);
                            break;
                        
                        case SmilConstants.COMPONENT_TYPE_IMAGE:
                         	ImageView imageView = new ImageView(getApplicationContext()); //(this);
                           	String imagePath = ((SMILImageComponent) component).getSrc();
                            Uri imageUri = Uri.parse(imagePath);
                            imageView.setImageURI(imageUri);
                            layout.addView(imageView);
                            mHandler.postDelayed(new RemoveTask(imageView), component.getDuration() * 1000);
                            break;
                    }
                }
            }
        }
        catch (Exception e)
        {
            Log.e("Exception", "error occurred while creating xml file", e);
        }
    }

    private class RemoveTask implements Runnable
    {
        private View view;


        public RemoveTask(View view)
        {
            this.view = view;
        }


        public void run()
        {
            view.setVisibility(View.INVISIBLE);
        }

    }

    private class RemoveAudio implements Runnable
    {
        private MediaPlayer mp;


        public RemoveAudio(MediaPlayer mediaPlayer)
        {
            mp = mediaPlayer;
        }


        public void run()
        {
            mp.stop();
        }
    }
}
