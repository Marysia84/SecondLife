package com.greensoft.secondlife;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.greensoft.secondlife._1.PeerId;

import org.json.JSONException;
import org.webrtc.MediaStream;
import org.webrtc.RendererCommon;
import org.webrtc.VideoRenderer;
import org.webrtc.VideoRendererGui;

import static com.greensoft.secondlife.MainActivity.REMOTE_HEIGHT;
import static com.greensoft.secondlife.MainActivity.REMOTE_WIDTH;
import static com.greensoft.secondlife.MainActivity.REMOTE_X;
import static com.greensoft.secondlife.MainActivity.REMOTE_Y;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CameraFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CameraFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CameraFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_REMOTE_PEER_ID = "REMOTE_PEER_ID";

    // TODO: Rename and change types of parameters
    private String remotePeerId;

    private VideoRenderer.Callbacks remoteRender;
    private RendererCommon.ScalingType scalingType = RendererCommon.ScalingType.SCALE_ASPECT_FILL;
    private GLSurfaceView vsv;
    private boolean visible = false;

    public CameraFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static CameraFragment newInstance(String remotePeerId) {
        CameraFragment fragment = new CameraFragment();
        Bundle args = new Bundle();
        args.putString(ARG_REMOTE_PEER_ID, remotePeerId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            remotePeerId = getArguments().getString(ARG_REMOTE_PEER_ID);
            if(visible){
                start();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_demo, container, false);

        vsv = (GLSurfaceView) view.findViewById(R.id.glFragmentSurfaceView);
        vsv.setPreserveEGLContextOnPause(true);
        vsv.setKeepScreenOn(true);
        VideoRendererGui.setView(vsv, new Runnable() {
            @Override
            public void run() {
                //init();
            }
        });

        remoteRender = VideoRendererGui.create(
                REMOTE_X, REMOTE_Y,
                REMOTE_WIDTH, REMOTE_HEIGHT, scalingType, false);

        return view;
    }

    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        this.visible = visible;
    }

    public void start() {

        try {
            final CameraViewActivity activity = (CameraViewActivity) getActivity();
            activity.requestRemoteStream(new PeerId(remotePeerId));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public void onRemoteStream(MediaStream remoteMediaStream) {

        remoteMediaStream.videoTracks.get(0).addRenderer(new VideoRenderer(remoteRender));
        boolean mirror = false;
        VideoRendererGui.update(remoteRender,
                REMOTE_X, REMOTE_Y,
                REMOTE_WIDTH, REMOTE_HEIGHT, scalingType, mirror);
    }
}
