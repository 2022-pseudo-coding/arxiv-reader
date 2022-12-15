package com.example.arxivreader;

import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.View;

import com.example.arxivreader.model.db.CanvasDao;
import com.example.arxivreader.model.db.PaperDao;
import com.example.arxivreader.model.db.PaperDatabase;
import com.example.arxivreader.ui.vm.CanvasViewModel;
import com.example.arxivreader.ui.vm.DirViewModel;
import com.google.android.material.navigation.NavigationView;

import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.arxivreader.databinding.ActivityMainBinding;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    private DirViewModel dirViewModel;
    private CanvasViewModel canvasViewModel;

    private static ExecutorService dbExecutor = Executors.newSingleThreadExecutor();
    private static PaperDao paperDao;
    private static CanvasDao canvasDao;
    private static Handler dbHandler = new Handler();

    public static void dbExecute(Runnable r){
        dbExecutor.execute(r);
    }

    public static void dbHandle(Runnable r){
        dbHandler.post(r);
    }

    public static CanvasDao getCanvasDao() {
        return canvasDao;
    }

    public static PaperDao getPaperDao() {
        return paperDao;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dirViewModel = new ViewModelProvider(this).get(DirViewModel.class);
        canvasViewModel = new ViewModelProvider(this).get(CanvasViewModel.class);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_search, R.id.nav_upload, R.id.nav_canvas)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        /**
         * database init frag
         */
        dbExecutor.execute(() -> {
            PaperDatabase db = Room.databaseBuilder(getApplicationContext(),
                    PaperDatabase.class, "paperdb").fallbackToDestructiveMigration().build();
            paperDao = db.paperDao();
            canvasDao = db.canvasDao();
            dirViewModel.getDirMapLiveData().postValue(paperDao.getDirAndPapers());
            canvasViewModel.getCanvasListLiveData().postValue(canvasDao.getAllCanvas());
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}