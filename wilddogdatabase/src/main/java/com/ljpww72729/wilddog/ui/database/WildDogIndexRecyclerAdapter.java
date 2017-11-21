package com.ljpww72729.wilddog.ui.database;

import android.arch.lifecycle.LifecycleOwner;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;

import com.wilddog.client.DataSnapshot;
import com.wilddog.client.Query;
import com.wilddog.client.SyncReference;

public abstract class WildDogIndexRecyclerAdapter<T, VH extends RecyclerView.ViewHolder>
        extends WildDogRecyclerAdapter<T, VH> {
    /**
     * @param parser   a custom {@link SnapshotParser} to convert a {@link DataSnapshot} to the
     *                 model class
     * @param keyQuery The Firebase location containing the list of keys to be found in {@code
     *                 dataRef}. Can also be a slice of a location, using some combination of {@code
     *                 limit()}, {@code startAt()}, and {@code endAt()}. <b>Note, this can also be a
     *                 {@link com.wilddog.client.SyncReference}.</b>
     * @param dataRef  The Firebase location to watch for data changes. Each key key found at {@code
     *                 keyQuery}'s location represents a list item in the {@link RecyclerView}.
     * @see WildDogRecyclerAdapter#WildDogRecyclerAdapter(ObservableSnapshotArray, int, Class,
     * LifecycleOwner)
     */
    public WildDogIndexRecyclerAdapter(SnapshotParser<T> parser,
                                       @LayoutRes int modelLayout,
                                       Class<VH> viewHolderClass,
                                       Query keyQuery,
                                       SyncReference dataRef,
                                       LifecycleOwner owner) {
        super(new WildDogIndexArray<>(keyQuery, dataRef, parser),
                modelLayout,
                viewHolderClass,
                owner);
    }

    /**
     * @see #WildDogIndexRecyclerAdapter(SnapshotParser, int, Class, Query, SyncReference,
     * LifecycleOwner)
     */
    public WildDogIndexRecyclerAdapter(SnapshotParser<T> parser,
                                       @LayoutRes int modelLayout,
                                       Class<VH> viewHolderClass,
                                       Query keyQuery,
                                       SyncReference dataRef) {
        super(new WildDogIndexArray<>(keyQuery, dataRef, parser), modelLayout, viewHolderClass);
    }

    /**
     * @see #WildDogIndexRecyclerAdapter(SnapshotParser, int, Class, Query, SyncReference,
     * LifecycleOwner)
     */
    public WildDogIndexRecyclerAdapter(Class<T> modelClass,
                                       @LayoutRes int modelLayout,
                                       Class<VH> viewHolderClass,
                                       Query keyQuery,
                                       SyncReference dataRef,
                                       LifecycleOwner owner) {
        this(new ClassSnapshotParser<>(modelClass),
                modelLayout,
                viewHolderClass,
                keyQuery,
                dataRef,
                owner);
    }

    /**
     * @see #WildDogIndexRecyclerAdapter(SnapshotParser, int, Class, Query, SyncReference)
     */
    public WildDogIndexRecyclerAdapter(Class<T> modelClass,
                                       @LayoutRes int modelLayout,
                                       Class<VH> viewHolderClass,
                                       Query keyQuery,
                                       SyncReference dataRef) {
        this(new ClassSnapshotParser<>(modelClass),
                modelLayout,
                viewHolderClass,
                keyQuery,
                dataRef);
    }
}
