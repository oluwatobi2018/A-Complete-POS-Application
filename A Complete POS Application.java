{
  "iss": "https://poynt.net",
  "iat": 1546627192,
  "exp": 1546628092,
  "sub": "urn:aid:f6a97531-d772-40a8-916e-4503bf188043",
  "poynt.biz": "e3038712-3a08-4a85-859e-c8e0c4b4509d",
  "poynt.uid": 34415083
}
sessionService.getCurrentUser(listener);

    private IPoyntSessionServiceListener listener = new IPoyntSessionServiceListener.Stub() {
        @Override
        public void onResponse(Account account, PoyntError poyntError) throws RemoteException {
            Log.d(TAG, account.name);
        }
    };
    private View.OnClickListener loginClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.d(TAG, "Login clicked");
            accountManager.getAuthToken(Constants.Accounts.POYNT_UNKNOWN_ACCOUNT,
                    Constants.Accounts.POYNT_AUTH_TOKEN, null, IntroActivity.this,
                    new OnUserLoginAttempt() , null);
        }
    };public class OnUserLoginAttempt implements AccountManagerCallback<Bundle>{
        public void run(AccountManagerFuture<Bundle> accountManagerFuture) {
            try {
                Bundle bundle = accountManagerFuture.getResult();
                String user = (String) bundle.get(AccountManager.KEY_ACCOUNT_NAME);
                String authToken = (String) bundle.get(AccountManager.KEY_AUTHTOKEN);
                if (authToken != null) {
                    Toast.makeText(IntroActivity.this, user + " successfully logged in", Toast.LENGTH_LONG).show();
                    Intent loginIntent = new Intent(IntroActivity.this, MainActivity.class);
                    startActivity(loginIntent);
                } else {
                    Toast.makeText(IntroActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                }
            } catch (OperationCanceledException e) {
                e.printStackTrace();
                Toast.makeText(IntroActivity.this, "Login cancelled", Toast.LENGTH_SHORT).show();
            } catch (IOException | AuthenticatorException e) {
                e.printStackTrace();
            }
        }
    }
    Payment payment = new Payment();
    payment.setAmount(amount);
    payment.setCurrency(currencyCode);

    // start Payment activity for result
    try {
        Intent collectPaymentIntent = new Intent(Intents.ACTION_COLLECT_PAYMENT);
        collectPaymentIntent.putExtra(Intents.INTENT_EXTRAS_PAYMENT, payment);
        startActivityForResult(collectPaymentIntent, COLLECT_PAYMENT_REQUEST);
    } catch (ActivityNotFoundException ex) {
        Log.e(TAG, "Poynt payment activity not found - did you install PoyntServices?", ex);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == COLLECT_PAYMENT_REQUEST){
            if (resultCode == Activity.RESULT_OK){
                final Payment payment = data.getParcelableExtra(Intents.INTENT_EXTRAS_PAYMENT);
                Log.d(TAG, "Received onPaymentAction from PaymentFragment w/ Status("
                        + payment.getStatus() + ")");
                // We just need to look at the payment status, the rest of the information is in the transaction object
                switch (payment.getStatus()) {
                    case COMPLETED:
                        if (payment.getTransactions() != null &&  payment.getTransactions().size() > 0) {
                            // Payment object has a list of transactions 
                        }
                        break;
                    case CANCELED:
                            // Payment was cancelled
                        break;
                        ...
                        ...
                        ...
                }
            } else if(resultCode == Activity.RESULT_CANCELED){
                // Payment was cancelled by the user
            }
        }
    }
    try {
        productService.getRegisterCatalogWithProducts(catalogWithProductListener);
        // Limit the use of this method since it fetches from the cloud, it is data intensive
        // productService.getRegisterCatalogWithProductsFromCloud(catalogWithProductListener);
    } catch (RemoteException e) {
        e.printStackTrace();
    }
    
    private IPoyntProductCatalogWithProductListener catalogWithProductListener = new IPoyntProductCatalogWithProductListener.Stub() {
        @Override
        public void onResponse(CatalogWithProduct catalogWithProduct, PoyntError poyntError) throws RemoteException {
            if (poyntError == null) {
                try {
                    // CatalogWithProduct has a list of CategoryWithProduct
                    // CategoryWithProduct has a list of CatalogItemWithProduct
                    // CatalogItemWithProduct has the actual Product
                } catch (Exception e) {
                    Log.d(TAG, e.toString());
                }
            } else {
                Log.e(TAG, poyntError.toString())
            }
        });
    };
    public static String getProductPatch(Product product) {
        List<JsonPatchElement> patchElements = new ArrayList<JsonPatchElement>();
        JsonArray patchArray = new JsonArray();
        if (product!= null) {
            // price
            if (product.getPrice() != null) {
                // update
                JsonPatchElement<Long> priceElement = new JsonPatchElement<>();
                priceElement.setOp("add");
                priceElement.setPath("/price/amount");
                priceElement.setValue(product.getPrice().getAmount());
                patchElements.add(priceElement);
            } else {
                // remove
                JsonPatchElement<Long> priceElement = new JsonPatchElement<>();
                priceElement.setOp("remove");
                priceElement.setPath("/price/amount");
                patchElements.add(priceElement);
            }
        }
        JsonElement patch = new Gson().toJsonTree(patchElements);
        return patch.toString();
    }
    productService.updateProduct(product.getId(), getProductPatch(product), productServiceListener);
    OrderItem item = new OrderItem();
    item.setName(product.getName());
    item.setProductId(product.getId());
    item.setUnitPrice(product.getPrice().getAmount());
    item.setQuantity(quantity);
    item.setDetails(product.getDescription());
    item.setSku(product.getSku());
    item.setStatus(OrderItemStatus.ORDERED);
    OrderAmounts amounts = new OrderAmounts();
for (OrderItem item : items){
    Float quantity = item.getQuantity();
    float itemPrice = (item.getUnitPrice() * item.getQuantity());
    float itemsfee = 0;

    // Add item Fees
    if (item.getFees() != null && item.getFees().size() > 0){
        for (Fee fee : item.getFees()){
            if (fee.getPercentage() != null){
                itemsfee += itemPrice * fee.getPercentage();
            }else {
                itemsfee += fee.getAmount();
            }
        }
        itemsFeeTotal = (long)itemsfee;
    }else{
        if(item.getFee() != null) {
            itemsFeeTotal += (long) (quantity * item.getFee());
        }
    }

    // Add item discounts
    if(item.getDiscount() != null) {
        itemsDiscountTotal -= item.getDiscount();
    }

    // Add item Taxes
    itemsTaxTotal += getItemTax(item);  // refer to the sample POS app
    
    itemsSubTotal += itemPrice;
}

// Set the totals to the amounts
amounts.setFeeTotal(itemsFeeTotal);
amounts.setDiscountTotal(itemsDiscountTotal);
amounts.setTaxTotal(itemsTaxTotal);
amounts.setSubTotal(itemsSubTotal);

itemsNetTotal = itemsSubTotal + itemsFeeTotal + itemsDiscountTotal + itemsTaxTotal;
amounts.setNetTotal(itemsNetTotal);
orderService.createOrder(order, UUID.randomUUID().toString(), createOrderListener);
orderService.updateOrder(orderId, order, UUID.randomUUID().toString(), updateOrderLister);
order.setTransactions(payment.getTransactions());
orderService.completeOrder(orderId, order, UUID.randomUUID().toString(), completeOrderListener);
String[] mProjection = OrderstatusesColumns.FULL_PROJECTION;
String mSelectionClause = OrderstatusesColumns.FULFILLMENTSTATUS + "= ?";
String[] mSelectionArgs = {OrderStatus.OPENED.status()};
String mSortOrder = null;
Cursor cursor = getContentResolver().query(OrdersColumns.CONTENT_URI_WITH_NETTOTAL_TRXN_STATUS,
                mProjection, mSelectionClause, mSelectionArgs, mSortOrder);
OrdersCursor orderCursor = new OrdersCursor(cursor);
if (orderCursor != null) {
    if (orderCursor.getCount() > 0) {
        while (orderCursor.moveToNext()) {
            orderId = orderCursor.getOrderid();
            Log.d(TAG, "order id: " + orderId);
            Log.d(TAG, "customer user id: " + orderCursor.getCustomeruserid());
            Log.d(TAG, "order number: " + orderCursor.getOrdernumber());
        }
    }
    orderCursor.close();
    cursor.close();
}
// "MULTI" or "SINGLE" allows for multiple product scans in a single go
intent.putExtra("MODE", "SINGLE");
// if multi mode - also register the receiver
IntentFilter scannerIntentFilter = new IntentFilter();
startActivityForResult(intent, SCANNER_REQUEST_CODE);

// If multi scan mode is enabled register for a broadcast to receive the codes
scannerIntentFilter.addAction("poynt.intent.action.SCANNER_RESULT");
registerReceiver(scanResultReceiver, scannerIntentFilter);
// Check which request we're responding to
if (requestCode == SCANNER_REQUEST_CODE) {
    // Make sure the request was successful
    if (resultCode == RESULT_OK) {
        // get scan results
        String code = data.getStringExtra("CODE");
        String format = data.getStringExtra("FORMAT");
    } else if (resultCode == RESULT_CANCELED) {
        Log.d(TAG, "Result canceled");
    }
    // unregister for the broadcast receiver if you have previously registered for
    unregisterReceiver(scanResultReceiver);
}
Bundle options = new Bundle();
options.putString(Intents.EXTRA_EMAIL, "Enter your email");
options.putString(EXTRA_LEFT_BUTTON_TITLE, "TAKE MY EMAIL");
options.putString(EXTRA_RIGHT_BUTTON_TITLE, "GIVE ME PRIVACY");
secondScreenService.captureEmail(options, new IPoyntEmailEntryListener.Stub() {
    @Override
    public void onEmailEntered(String s) throws RemoteException {
        Log.d(TAG, "Email entered : " + s);
    }
    @Override
    public void onCancel() throws RemoteException {
        Log.d(TAG, "Email entry cancelled");
    }
});
context.bindService(Intents.getComponentIntent(
                Intents.COMPONENT_POYNT_RECEIPT_PRINTING_SERVICE), connection, Context.BIND_AUTO_CREATE);
...
...
private ServiceConnection connection = new ServiceConnection() {
    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        Log.d(TAG, "Receipt printing service connected");
        if (iBinder != null) {
            receiptPrintingService = IPoyntReceiptPrintingService.Stub.asInterface(iBinder);
        }
    }
    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        Log.d(TAG, "Receipt Printing service disconnected");
        receiptPrintingService = null;
    }
};
String jobId = UUID.randomUUID().toString();
receiptPrintingService.printTransactionReceipt(jobId, transactionId, 0, receiptPrintListener);
private IPoyntReceiptPrintingServiceListener receiptPrintListener = new IPoyntReceiptPrintingServiceListener.Stub() {
    @Override
    public void printQueued() throws RemoteException {
        Log.d(TAG, "Printing  queued");
    }

    @Override
    public void printFailed(PrinterStatus printerStatus) throws RemoteException {
        Log.d(TAG, "Printing failed with status " + printerStatus.getMessage());
    }
};
String jobId = UUID.randomUUID().toString();
receiptPrintingService.printOrderReceipt(jobId, orderId, receiptPrintListener);
Intent intent = new Intent(Intents.ACTION_DISPLAY_PAYMENT);
intent.putExtra(Intents.INTENT_EXTRAS_TRANSACTION_ID, transactionId);
startActivity(intent);
<receiver android:name="com.my.android.package.MyBroadcastReceiver"
          android:enabled="true"
          android:exported="true">
    <intent-filter>
        <action android:name="poynt.intent.action.CLOUD_MESSAGE_RECEIVED" />
        <category android:name="poynt.category.CLOUD_MESSAGE" />
    </intent-filter>
</receiver>
public class MyBroadcastReceiver extends BroadcastReceiver {
    public MyBroadcastReceiver() {}
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("MyBroadcastReceiver", "Got cloud Message: " + intent.getStringExtra(Intents
            .INTENT_EXTRA_CLOUD_MESSAGE_BODY));
    }
}