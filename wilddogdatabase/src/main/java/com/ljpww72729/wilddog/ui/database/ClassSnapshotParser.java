package com.ljpww72729.wilddog.ui.database;

import android.support.annotation.NonNull;

import com.wilddog.client.DataSnapshot;


/**
 * A convenience implementation of {@link SnapshotParser} that converts a {@link DataSnapshot} to
 * the parametrized class via {@link DataSnapshot#getValue(Class)}.
 *
 * @param <T> the POJO class to create from snapshots.
 */
public class ClassSnapshotParser<T> implements SnapshotParser<T> {
    private Class<T> mClass;

    public ClassSnapshotParser(@NonNull Class<T> clazz) {
        mClass = Preconditions.checkNotNull(clazz);
    }

    @Override
    public T parseSnapshot(DataSnapshot snapshot) {
        return (T) snapshot.getValue(mClass);
    }
}
