package com.ambabovpn.tunnel.aidl;

public interface IUltraSSHServiceInternal extends android.os.IInterface
{
    /** Local-side IPC implementation stub class. */
    public static abstract class Stub extends android.os.Binder implements com.ambabovpn.tunnel.aidl.IUltraSSHServiceInternal
    {
        private static final String DESCRIPTOR = "com.ambabo.ultrasshservice.aidl.IUltraSSHServiceInternal";
        /** Construct the stub at attach it to the interface. */
        public Stub()
        {
            this.attachInterface(this, DESCRIPTOR);
        }
        /**
         * Cast an IBinder object into an com.ambabo.ultrasshservice.aidl.IUltraSSHServiceInternal interface,
         * generating a proxy if needed.
         */
        public static com.ambabovpn.tunnel.aidl.IUltraSSHServiceInternal asInterface(android.os.IBinder obj)
        {
            if ((obj==null)) {
                return null;
            }
            android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (((iin!=null)&&(iin instanceof com.ambabovpn.tunnel.aidl.IUltraSSHServiceInternal))) {
                return ((com.ambabovpn.tunnel.aidl.IUltraSSHServiceInternal)iin);
            }
            return new com.ambabovpn.tunnel.aidl.IUltraSSHServiceInternal.Stub.Proxy(obj);
        }
        @Override public android.os.IBinder asBinder()
        {
            return this;
        }
        @Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
        {
            String descriptor = DESCRIPTOR;
            switch (code)
            {
                case INTERFACE_TRANSACTION:
                    {
                        reply.writeString(descriptor);
                        return true;
                    }
                case TRANSACTION_stopVPN:
                    {
                        data.enforceInterface(descriptor);
                        this.stopVPN();
                        reply.writeNoException();
                        return true;
                    }
                default:
                    {
                        return super.onTransact(code, data, reply, flags);
                    }
            }
        }
        private static class Proxy implements com.ambabovpn.tunnel.aidl.IUltraSSHServiceInternal
        {
            private android.os.IBinder mRemote;
            Proxy(android.os.IBinder remote)
            {
                mRemote = remote;
            }
            @Override public android.os.IBinder asBinder()
            {
                return mRemote;
            }
            public String getInterfaceDescriptor()
            {
                return DESCRIPTOR;
            }
            /**
             * @param replaceConnection True if the VPN is connected by a new connection.
             * @return true if there was a process that has been send a stop signal
             */
            @Override public void stopVPN() throws android.os.RemoteException
            {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    mRemote.transact(Stub.TRANSACTION_stopVPN, _data, _reply, 0);
                    _reply.readException();
                }
                finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }
        static final int TRANSACTION_stopVPN = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
    }
    /**
     * @param replaceConnection True if the VPN is connected by a new connection.
     * @return true if there was a process that has been send a stop signal
     */
    public void stopVPN() throws android.os.RemoteException;
}
