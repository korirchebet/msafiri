package com.elitechinc.my.Adapters;

import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.elitechinc.my.Classes.Hotspots;
import com.elitechinc.my.MainActivity;
import com.elitechinc.my.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.SphericalUtil;

import org.w3c.dom.Text;

public class HotspotAdapter extends FirebaseRecyclerAdapter<Hotspots, HotspotAdapter.hotspotViewholder> {
    public HotspotAdapter(@NonNull FirebaseRecyclerOptions<Hotspots> options) {
        super(options);
    }

    Double distanceme;

    @Override
    protected void onBindViewHolder(@NonNull HotspotAdapter.hotspotViewholder holder, int position, @NonNull Hotspots model) {
        holder.name.setText(model.getName());
        String keybro = getRef(position).getKey();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("LocUpdates").child("Realtime");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                LatLng hotspot = new LatLng(model.getLatitude(), model.getLongitude());
                Double latme = snapshot.child("latitude").getValue(Double.class);
                Double longme = snapshot.child("longitude").getValue(Double.class);
                LatLng me = new LatLng(latme, longme);
                distanceme = SphericalUtil.computeDistanceBetween(hotspot, me);
                String stringdouble = Double.toString(distanceme);
                holder.distance.setText(String.format("%.2f", distanceme / 1000) + "km" + " " + "away");
                String ditme = String.format("%.2f", distanceme / 1000) + "km" + " " + "away";
                //int num = Integer.parseInt(String.format("%.2f", distanceme / 1000));
                // DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Distance");
                //databaseReference1.push().setValue();
                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Hotspots").child(keybro);
                        databaseReference1.removeValue();
                        return false;
                    }
                });
                if ((distanceme / 1000) <= 1) {
                    holder.danger.setVisibility(View.VISIBLE);
                    Intent intent = new Intent(holder.itemView.getContext(), MainActivity.class);
                    String CHANNEL_ID = "MYCHANNEL";
                    NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "name", NotificationManager.IMPORTANCE_LOW);
                    PendingIntent pendingIntent = PendingIntent.getActivity(holder.itemView.getContext(), 1, intent, 0);
                    Notification notification = new Notification.Builder(holder.itemView.getContext(), CHANNEL_ID)
                            .setContentText("Danger Ahead")
                            .setContentTitle("WARNING")
                            .setContentIntent(pendingIntent)
                            .addAction(android.R.drawable.sym_action_chat, "DANGER AHEAD", pendingIntent)
                            .setChannelId(CHANNEL_ID)
                            .setSmallIcon(android.R.drawable.sym_action_chat)
                            .build();
                    NotificationManager notificationManager = (NotificationManager) holder.itemView.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.createNotificationChannel(notificationChannel);
                    notificationManager.notify(1, notification);
                    Vibrator v = (Vibrator) holder.itemView.getContext().getSystemService(Context.VIBRATOR_SERVICE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                    } else {
                        v.vibrate(500);
                    }

                }
                //DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Details").child(keybro);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @NonNull
    @Override
    public HotspotAdapter.hotspotViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hotspots, parent, false);
        return new HotspotAdapter.hotspotViewholder(view);
    }

    public class hotspotViewholder extends RecyclerView.ViewHolder {
        TextView name, distance, danger;

        public hotspotViewholder(@NonNull View itemView) {
            super(itemView);
            distance = itemView.findViewById(R.id.distance);
            name = itemView.findViewById(R.id.name);
            danger = itemView.findViewById(R.id.danger);

        }
    }
}
