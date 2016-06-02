package com.swx.softdraft;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.SeekBar;
import android.widget.TextView;

import com.swx.softdraft.widget.CircleWaveView;

public class CircleWaveActivity extends AppCompatActivity {
    private CircleWaveView circleWaveView;

    private TextView tvWave;
    private TextView tvAm;
    private TextView tvDelay;
    private TextView tvDx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_wave);
        init();
    }

    private void init() {
        circleWaveView = (CircleWaveView) findViewById(R.id.wav_wave);
        SeekBar seekBar = (SeekBar) findViewById(R.id.sb_dx);
        tvWave = (TextView) findViewById(R.id.tv_wave);
        tvAm = (TextView) findViewById(R.id.tv_am);
        tvDx = (TextView) findViewById(R.id.tv_dx);
        tvDelay = (TextView) findViewById(R.id.tv_delay);
        String str;
        str = getString(R.string.wave_c) + ": " + circleWaveView.getWaveCoefficient();
        tvWave.setText(str);
        str = getString(R.string.am_c) + ": " + circleWaveView.getAmplitudeCoefficient();
        tvAm.setText(str);
        str = getString(R.string.inc_dx) + ": " + circleWaveView.getWaveStep();
        tvDx.setText(str);
        str = getString(R.string.refresh_delay) + ": " + circleWaveView.getDrawDelay();
        tvDelay.setText(str);
        if (seekBar != null) {
            seekBar.setProgress(circleWaveView.getWaveStep());
            seekBar.setOnSeekBarChangeListener(listener);
        }
        seekBar = (SeekBar) findViewById(R.id.sb_draw_delay);
        if (seekBar != null) {
            seekBar.setProgress(circleWaveView.getDrawDelay());
            seekBar.setOnSeekBarChangeListener(listener);
        }
        seekBar = (SeekBar) findViewById(R.id.sb_amplitude);
        if (seekBar != null) {
            seekBar.setProgress(circleWaveView.getAmplitudeCoefficient());
            seekBar.setOnSeekBarChangeListener(listener);
        }
        seekBar = (SeekBar) findViewById(R.id.sb_wavec);
        if (seekBar != null) {
            seekBar.setProgress(circleWaveView.getWaveCoefficient());
            seekBar.setOnSeekBarChangeListener(listener);
        }
    }

    SeekBar.OnSeekBarChangeListener listener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            int progress = seekBar.getProgress();
            String str = null;
            switch (seekBar.getId()) {
                case R.id.sb_amplitude: {
                    circleWaveView.setAmplitudeCoefficient(progress);
                    str = getString(R.string.am_c) + ": " + circleWaveView.getAmplitudeCoefficient();
                    tvAm.setText(str);
                }
                break;
                case R.id.sb_dx: {
                    circleWaveView.setDx(progress);
                    str = getString(R.string.inc_dx) + ": " + circleWaveView.getWaveStep();
                    tvDx.setText(str);
                }
                break;
                case R.id.sb_wavec: {
                    circleWaveView.setWaveCoefficient(progress);
                    str = getString(R.string.wave_c) + ": " + circleWaveView.getWaveCoefficient();
                    tvWave.setText(str);
                }
                break;
                case R.id.sb_draw_delay: {
                    circleWaveView.setDrawDelay(progress);
                    str = getString(R.string.refresh_delay) + ": " + circleWaveView.getDrawDelay();
                    tvDelay.setText(str);
                }
                break;
                default:
                    break;
            }
        }
    };

}
