package com.ljpww72729.wilddog.ui.database;

import android.arch.lifecycle.LifecycleOwner;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.widget.ListView;

import com.wilddog.client.DataSnapshot;
import com.wilddog.client.Query;
import com.wilddog.client.SyncReference;


public abstract class WildDogIndexListAdapter<T> extends WildDogListAdapter<T> {
    /**
     * @param parser   a custom {@link SnapshotParser} to convert a {@link DataSnapshot} to the
     *                 model class
     * @param keyQuery The Firebase location containing the list of keys to be found in {@code
     *                 dataRef}. Can also be a slice of a location, using some combination of {@code
     *                 limit()}, {@code startAt()}, and {@code endAt()}. <b>Note, this can also be a
     *                 {@link SyncReference}.</b>
     * @param dataRef  The Firebase location to watch for data changes. Each key key found at {@code
     *                 keyQuery}'s location represents a list item in the {@link ListView}.
     * @see WildDogListAdapter#WildDogListAdapter(Context, ObservableSnapshotArray, int,
     * LifecycleOwner)
     */
    public WildDogIndexListAdapter(Context context,
                                   SnapshotParser<T> parser,
                                   @LayoutRes int modelLayout,
                                   Query keyQuery,
                                   SyncReference dataRef,
                                   LifecycleOwner owner) {
        super(context, new WildDogIndexArray<>(keyQuery, dataRef, parser), modelLayout, owner);
    }

    /**
     * @see #WildDogIndexListAdapter(Context, SnapshotParser, int, Query, SyncReference,
     * LifecycleOwner)
     */
    public WildDogIndexListAdapter(Context context,
                                   SnapshotParser<T> parser,
                                   @LayoutRes int modelLayout,
                                   Query keyQuery,
                                   SyncReference dataRef) {
        super(context, new WildDogIndexArray<>(keyQuery, dataRef, parser), modelLayout);
    }

    /**
     * @see #WildDogIndexListAdapter(Context, SnapshotParser, int, Query, SyncReference,
     * LifecycleOwner)
     */
    public WildDogIndexListAdapter(Context context,
                                   Class<T> modelClass,
                                   @LayoutRes int modelLayout,
                                   Query keyQuery,
                                   SyncReference dataRef,
                                   LifecycleOwner owner) {
        this(context, new ClassSnapshotParser<>(modelClass), modelLayout, keyQuery, dataRef, owner);
    }

    /**
     * @see #WildDogIndexListAdapter(Context, SnapshotParser, int, Query, SyncReference)
     */
    public WildDogIndexListAdapter(Context context,
                                   Class<T> modelClass,
                                   @LayoutRes int modelLayout,
                                   Query keyQuery,
                                   SyncReference dataRef) {
        this(context, new ClassSnapshotParser<>(modelClass), modelLayout, keyQuery, dataRef);
    }
}
