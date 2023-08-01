package com.ambabovpn.pro.util;

import android.os.AsyncTask;

public class Fetch extends AsyncTask<Void, Integer, String> {
    private final WorkerAction workerAction;

    public Fetch(WorkerAction workerAction) {
        this.workerAction = workerAction;
    }

    /**
     * Override this method to perform a computation on a background thread. The
     * specified parameters are the parameters passed to {@link #execute}
     * by the caller of this task.
     * <p>
     * This will normally run on a background thread. But to better
     * support testing frameworks, it is recommended that this also tolerates
     * direct execution on the foreground thread, as part of the {@link #execute} call.
     * <p>
     * This method can call {@link #publishProgress} to publish updates
     * on the UI thread.
     *
     * @param voids The parameters of the task.
     * @return A result, defined by the subclass of this task.
     * @see #onPreExecute()
     * @see #onPostExecute
     * @see #publishProgress
     */
    @Override
    protected String doInBackground(Void... voids) {
        workerAction.runFirst();
        return null;
    }

    @Override
    protected void onPostExecute(String str) {
        workerAction.runLast();
    }
}
