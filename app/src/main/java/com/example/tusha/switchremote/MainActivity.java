package com.example.tusha.switchremote;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.hardware.ConsumerIrManager;
import android.view.View;
import android.widget.ImageButton;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private final static String CMD_LIGHT1 =
            "0000 006E 0000 0000 0020 0020 0020 0020 0040 0040 0020 0020 0040 0040 0020 0020 0040 0020 0020 0020 0020 0040 0040 0CAA";
    private final static String CMD_LIGHT2 =
            "0000 006E 0000 0000 0020 0020 0040 0020 0020 0040 0020 0020 0018 0068 0018 0068 0015 004D 0001 003E 0005 005B 0017 0C93 0020 0020 0040 0020 0020 0040 0020 0020 002F 0051 002E 0052 002E 0033 001A 0026 0020 0040 0020 0C8A 0020 0020 0040 0020 0020 0040 0020 0020 0040 0040 0040 0040 0040 0020 0020 0020 0020 0040 0020 29CC";
    private final static String CMD_LIGHT3 =
            "0000 006E 0000 0000 0020 0020 001C 0024 0006 0F64";
    private final static String CMD_LIGHT4 =
            "0000 006E 0000 0000 0020 0020 0018 0F93";
    private final static String CMD_FAN =
            "0000 006E 0000 0000 0020 0020 0020 0020 0028 0058 0014 002C 000A 0076 000A 0036 0003 007D 0007 00BA 000A 0CA0 0020 0020 0020 0020 0028 0058 0014 002C 000A 0076 0009 0037 0003 007D 0007 00B9 000A 0CA0 0020 0020 0020 0020 0024 005C 0014 002C 000A 0076 0009 0037 0003 007D 0007 00B9 0009 29CC";
    private final static String CMD_FANUP =
            "0000 006E 0000 0000 0020 0020 003A 0027 000D 0053 0011 002F 0006 007A 0004 007C 0003 0DA8 0020 0020 003A 0027 000D 0053 0011 002F 0007 0079 0007 0079 0006 0DA5 0020 0020 0040 0021 0016 004A 001A 0026 000F 0071 000E 0072 000D 0033 0007 0039 0004 003D 0001 29CC";
    private final static String CMD_FANDOWN =
            "0000 006E 0000 0000 0020 0020 0020 0020 0029 0057 0016 002A 000C 0074 000D 00B3 000F 0031 0008 0038 0005 003B 0003 0CA7 0020 0020 0020 0020 0040 0041 0020 0020 0034 004D 0034 002D 001F 0041 0020 0020 0020 0020 0020 0020 0020 0C8A 0020 0020 0020 0020 0040 0041 0020 0020 0029 0057 002A 0037 0015 004B 0020 0020 0020 0020 0020 0020 001C 29CC";
    private final static String CMD_ALLON =
            "0000 006E 0000 0000 0020 0020 0040 0020 001D 0043 0020 0020 0016 006A 0015 004D 0002 005D 0017 0029 000F 0071 0013 0C97 0020 0020 0040 0020 001D 0043 0020 0020 0016 006A 0018 0048 0005 005B 001A 0026 0012 006E 0016 0C94 0020 0020 0040 0020 001D 0043 0020 0020 0024 005C 0023 003E 000E 0052 0020 0020 001B 0065 001F 29CC";
    private final static String CMD_ALLOFF =
            "0000 006E 0000 0000 0002 29CC";

    private ConsumerIrManager irManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        irManager = (ConsumerIrManager) getSystemService(CONSUMER_IR_SERVICE);


        findViewById(R.id.imgButton1).setOnClickListener(new ClickListener(hex2ir(CMD_LIGHT1)));
        findViewById(R.id.imgButton2).setOnClickListener(new ClickListener(hex2ir(CMD_LIGHT2)));
        findViewById(R.id.imgButton3).setOnClickListener(new ClickListener(hex2ir(CMD_LIGHT3)));
        findViewById(R.id.imgButton4).setOnClickListener(new ClickListener(hex2ir(CMD_LIGHT4)));
        findViewById(R.id.imgButtonup).setOnClickListener(new ClickListener(hex2ir(CMD_FANUP)));
        findViewById(R.id.imgButtondown).setOnClickListener(new ClickListener(hex2ir(CMD_FANDOWN)));
        findViewById(R.id.imgButtonfan).setOnClickListener(new ClickListener(hex2ir(CMD_FAN)));
        findViewById(R.id.imgButtonallon).setOnClickListener(new ClickListener(hex2ir(CMD_ALLON)));
        findViewById(R.id.imgButtonalloff).setOnClickListener(new ClickListener(hex2ir(CMD_ALLOFF)));

    }
    private IRCommand hex2ir(final String irData) {
        List<String> list = new ArrayList<String>(Arrays.asList(irData.split(" ")));
        list.remove(0); // dummy
        int frequency = Integer.parseInt(list.remove(0), 16); // frequency
        list.remove(0); // seq1
        list.remove(0); // seq2

        frequency = (int) (1000000 / (frequency * 0.241246));
        int pulses = 1000000 / frequency;
        int count;

        int[] pattern = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            count = Integer.parseInt(list.get(i), 16);
            pattern[i] = count * pulses;
        }

        return new IRCommand(frequency, pattern);
    }

    private class ClickListener implements View.OnClickListener {
        private final IRCommand cmd;

        public ClickListener(final IRCommand cmd) {
            this.cmd = cmd;
        }

        @Override
        public void onClick(final View view) {
            android.util.Log.d("Remote", "frequency: " + cmd.freq);
            android.util.Log.d("Remote", "pattern: " + Arrays.toString(cmd.pattern));
            irManager.transmit(cmd.freq, cmd.pattern);
        }
    }

    class IRCommand {
        private final int freq;
        private final int[] pattern;

        private IRCommand(int freq, int[] pattern) {
            this.freq = freq;
            this.pattern = pattern;
        }
    }
}
