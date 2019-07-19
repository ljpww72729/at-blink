package com.ljpww72729.wilddog.ui.database;


import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.OnLifecycleEvent;
import android.support.annotation.LayoutRes;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wilddog.client.DataSnapshot;
import com.wilddog.client.Query;
import com.wilddog.client.SyncError;
import com.wilddog.client.SyncReference;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import androidx.recyclerview.widget.RecyclerView;

/**
 * This class is a generic way of backing a {@link RecyclerView} with a Firebase location. It
 * handles all of the child events at the given Firebase location and marshals received data into
 * the given class type.
 * <p>
 * See the <a href="https://github.com/firebase/FirebaseUI-Android/blob/master/database/README.md">README</a>
 * for an in-depth tutorial on how to set up the FirebaseRecyclerAdapter.
 *
 * @param <T>  The Java class that maps to the type of objects stored in the Firebase location.
 * @param <VH> The {@link RecyclerView.ViewHolder} class that contains the Views in the layout that
 *             is shown for each object.
 */
public abstract class WildDogRecyclerAdapter<T, VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH> implements WildDogAdapter<T> {
    private static final String TAG = "FirebaseRecyclerAdapter";

    protected final ObservableSnapshotArray<T> mSnapshots;
    protected final Class<VH> mViewHolderClass;
    protected final int mModelLayout;

    /**
     * @param snapshots       The data used to populate the adapter
     * @param modelLayout     This is the layout used to represent a single item in the list. You
     *                        will be responsible for populating an instance of the corresponding
     *                        view with the data from an instance of modelClass.
     * @param viewHolderClass The class that hold references to all sub-views in an instance
     *                        modelLayout.
     * @param owner           the lifecycle owner used to automatically listen and cleanup after
     *                        {@link FragmentActivity#onStart()} and {@link FragmentActivity#onStop()}
     *                        events reflectively.
     */
    public WildDogRecyclerAdapter(ObservableSnapshotArray<T> snapshots,
                                  @LayoutRes int modelLayout,
                                  Class<VH> viewHolderClass,
                                  LifecycleOwner owner) {
        mSnapshots = snapshots;
        mViewHolderClass = viewHolderClass;
        mModelLayout = modelLayout;

        if (owner != null) { owner.getLifecycle().addObserver(this); }
    }

    /**
     * @see #WildDogRecyclerAdapter(ObservableSnapshotArray, int, Class, LifecycleOwner)
     */
    public WildDogRecyclerAdapter(ObservableSnapshotArray<T> snapshots,
                                  @LayoutRes int modelLayout,
                                  Class<VH> viewHolderClass) {
        this(snapshots, modelLayout, viewHolderClass, null);
        startListening();
    }

    /**
     * @param parser a custom {@link SnapshotParser} to convert a {@link DataSnapshot} to the model
     *               class
     * @param query  The Firebase location to watch for data changes. Can also be a slice of a
     *               location, using some combination of {@code limit()}, {@code startAt()}, and
     *               {@code endAt()}. <b>Note, this can also be a {@link com.wilddog.client.SyncReference}.</b>
     * @see #WildDogRecyclerAdapter(ObservableSnapshotArray, int, Class)
     */
    public WildDogRecyclerAdapter(SnapshotParser<T> parser,
                                  @LayoutRes int modelLayout,
                                  Class<VH> viewHolderClass,
                                  Query query) {
        this(new WildDogArray<>(query, parser), modelLayout, viewHolderClass);
    }

    /**
     * @see #WildDogRecyclerAdapter(SnapshotParser, int, Class, Query)
     * @see #WildDogRecyclerAdapter(ObservableSnapshotArray, int, Class, LifecycleOwner)
     */
    public WildDogRecyclerAdapter(SnapshotParser<T> parser,
                                  @LayoutRes int modelLayout,
                                  Class<VH> viewHolderClass,
                                  Query query,
                                  LifecycleOwner owner) {
        this(new WildDogArray<>(query, parser), modelLayout, viewHolderClass, owner);
    }

    /**
     * @see #WildDogRecyclerAdapter(SnapshotParser, int, Class, Query)
     */
    public WildDogRecyclerAdapter(Class<T> modelClass,
                                  @LayoutRes int modelLayout,
                                  Class<VH> viewHolderClass,
                                  Query query) {
        this(new ClassSnapshotParser<>(modelClass), modelLayout, viewHolderClass, query);
    }

    /**
     * @see #WildDogRecyclerAdapter(SnapshotParser, int, Class, Query, LifecycleOwner)
     */
    public WildDogRecyclerAdapter(Class<T> modelClass,
                                  @LayoutRes int modelLayout,
                                  Class<VH> viewHolderClass,
                                  Query query,
                                  LifecycleOwner owner) {
        this(new ClassSnapshotParser<>(modelClass), modelLayout, viewHolderClass, query, owner);
    }

    @Override
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void startListening() {
        if (!mSnapshots.isListening(this)) {
            mSnapshots.addChangeEventListener(this);
        }
    }

    @Override
    public void cleanup() {
        mSnapshots.removeChangeEventListener(this);
        notifyDataSetChanged();
    }

    @SuppressWarnings("unused")
    @OnLifecycleEvent(Lifecycle.Event.ON_ANY)
    void cleanup(LifecycleOwner source, Lifecycle.Event event) {
        if (event == Lifecycle.Event.ON_STOP) {
            cleanup();
        } else if (event == Lifecycle.Event.ON_DESTROY) {
            source.getLifecycle().removeObserver(this);
        }
    }

    @Override
    public void onChildChanged(ChangeEventListener.EventType type,
                               DataSnapshot snapshot,
                               int index,
                               int oldIndex) {
        switch (type) {
            case ADDED:
                notifyItemInserted(index);
                break;
            case CHANGED:
                notifyItemChanged(index);
                break;
            case REMOVED:
                notifyItemRemoved(index);
                break;
            case MOVED:
                notifyItemMoved(oldIndex, index);
                break;
            default:
                throw new IllegalStateException("Incomplete case statement");
        }
    }

    @Override
    public void onDataChanged() {
    }

    @Override
    public void onCancelled(SyncError error) {
        Log.w(TAG, error.toException());
    }

    @Override
    public T getItem(int position) {
        return mSnapshots.getObject(position);
    }

    @Override
    public SyncReference getRef(int position) {
        return mSnapshots.get(position).getRef();
    }

    @Override
    public int getItemCount() {
        return mSnapshots.isListening(this) ? mSnapshots.size() : 0;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        try {
            Constructor<VH> constructor = mViewHolderClass.getConstructor(View.class);
            return constructor.newInstance(view);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mModelLayout;
    }

    @Override
    public void onBindViewHolder(VH viewHolder, int position) {
        T model = getItem(position);
        populateViewHolder(viewHolder, model, position);
    }

    /**
     * Each time the data at the given Firebase location changes, this method will be called for
     * each item that needs to be displayed. The first two arguments correspond to the mLayout and
     * mModelClass given to the constructor of this class. The third argument is the item's position
     * in the list.
     * <p>
     * Your implementation should populate the view using the data contained in the model.
     *
     * @param viewHolder The view to populate
     * @param model      The object containing the data used to populate the view
     * @param position   The position in the list of the view being populated
     */
    protected abstract void populateViewHolder(VH viewHolder, T model, int position);
}
