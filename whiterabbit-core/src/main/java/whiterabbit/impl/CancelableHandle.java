package whiterabbit.impl;

import whiterabbit.Cancelable;

class CancelableHandle implements Cancelable {
	private final Cancelable delegate;

	CancelableHandle(Cancelable delegate)
	{
		this.delegate = delegate;
	}

	@Override
	public void cancel() {
		delegate.cancel();
	}
}