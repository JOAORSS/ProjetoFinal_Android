package com.example.app06_materialss.localDatabase;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.app06_materialss.DAO.CarrinhoDao;
import com.example.app06_materialss.entity.PecaCarrinho;

@Database(entities = {PecaCarrinho.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract CarrinhoDao carrinhoDao();
    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                                    AppDatabase.class, "banco_app_pecas")
                                                    .build();
                }
            }
        }
        return INSTANCE;
    }
}
