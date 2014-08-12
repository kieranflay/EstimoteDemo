package com.estimote.examples.demos;

import android.app.Activity;
import android.nfc.FormatException;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.estimote.sdk.Beacon;
import com.estimote.sdk.connection.BeaconConnection;

/**
 * Demo that shows how to connect to beacon and change its minor value.
 *
 * @author wiktor@estimote.com (Wiktor Gworek)
 */
public class CharacteristicsDemoActivity extends Activity {

  private Beacon beacon;
  private BeaconConnection connection;

  private TextView statusView;
  private TextView beaconDetailsView;


  private EditText minorEditView;
  private EditText majorEditView;
  private EditText idEditView;


  private View afterConnectedViewMinor;
  private View afterConnectedViewMajor;
  private View afterConnectedViewId;


    @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.characteristics_demo);
    getActionBar().setDisplayHomeAsUpEnabled(true);

    statusView = (TextView) findViewById(R.id.status);
    beaconDetailsView = (TextView) findViewById(R.id.beacon_details);

    afterConnectedViewMinor = findViewById(R.id.after_connected_minor);
    afterConnectedViewMajor = findViewById(R.id.after_connected_major);
    afterConnectedViewId = findViewById(R.id.after_connected_id);

    minorEditView = (EditText) findViewById(R.id.minor);
    majorEditView = (EditText) findViewById(R.id.major);
    idEditView = (EditText) findViewById(R.id.id);

    beacon = getIntent().getParcelableExtra(ListBeaconsActivity.EXTRAS_BEACON);
    connection = new BeaconConnection(this, beacon, createConnectionCallback());

    findViewById(R.id.update_minor).setOnClickListener(createUpdateMinorButtonListener());
    findViewById(R.id.update_major).setOnClickListener(createUpdateMajorButtonListener());
    findViewById(R.id.update_id).setOnClickListener(createUpdateIdButtonListener());

  }

  @Override
  protected void onResume() {
    super.onResume();
    if (!connection.isConnected()) {
      statusView.setText("Status: Connecting...");
      connection.authenticate();
    }
  }

  @Override
  protected void onDestroy() {
    connection.close();
    super.onDestroy();
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      finish();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  /**
   * Returns click listener on update minor button.
   * Triggers update minor value on the beacon.
   */
  private View.OnClickListener createUpdateMinorButtonListener() {
    return new View.OnClickListener() {
      @Override public void onClick(View v) {
        int minor = parseMinorFromEditView();
        if (minor == -1) {
          showToast("Minor must be a number");
        } else {
          updateMinor(minor);

        }
      }
    };
  }

    private View.OnClickListener createUpdateMajorButtonListener() {
        return new View.OnClickListener() {
            @Override public void onClick(View v) {
                int major = parseMajorFromEditView();
                if (major == -1) {
                    showToast("Major must be a number");
                } else {
                    updateMajor(major);

                }
            }
        };
    }

    private View.OnClickListener createUpdateIdButtonListener() {
        return new View.OnClickListener() {
            @Override public void onClick(View v) {
                String id = String.valueOf(parseIdFromEditView());
                    updateId(id);
                }

        };
    }

  /**
   * @return Parsed integer from edit text view or -1 if cannot be parsed.
   */
  private int parseMinorFromEditView() {
    try {
      return Integer.parseInt(String.valueOf(minorEditView.getText()));
    } catch (NumberFormatException e) {
      return -1;
    }
  }

    private int parseMajorFromEditView() {
        try {
            return Integer.parseInt(String.valueOf(majorEditView.getText()));
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private String parseIdFromEditView() {
        return String.valueOf(idEditView.getText());
    }



  private void updateMinor(int minor) {
    // Minor value will be normalized if it is not in the range.
    // Minor should be 16-bit unsigned integer.
    connection.writeMinor(minor, new BeaconConnection.WriteCallback()
    {
      @Override public void onSuccess() {
        runOnUiThread(new Runnable() {
          @Override public void run() {
            showToast("Minor value updated");
          }
        });
      }

      @Override public void onError() {
        runOnUiThread(new Runnable() {
          @Override public void run() {
            showToast("Minor not updated");
          }
        });
      }
    });
  }

    private void updateMajor(int major) {
        // Minor value will be normalized if it is not in the range.
        // Minor should be 16-bit unsigned integer.
        connection.writeMajor(major, new BeaconConnection.WriteCallback() {
            @Override
            public void onSuccess() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showToast("Major value updated");
                    }
                });
            }

            @Override
            public void onError() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showToast("Major not updated");
                    }
                });
            }
        });
    }

    private void updateId(String id) {
        // Minor value will be normalized if it is not in the range.
        // Minor should be 16-bit unsigned integer.
        connection.writeProximityUuid(id, new BeaconConnection.WriteCallback()
        {
            @Override public void onSuccess() {
                runOnUiThread(new Runnable() {
                    @Override public void run() {
                        showToast("UUID value updated");
                    }
                });
            }

            @Override public void onError() {
                runOnUiThread(new Runnable() {
                    @Override public void run() {
                        showToast("UUID not updated");
                    }
                });
            }
        });
    }

  private BeaconConnection.ConnectionCallback createConnectionCallback() {
    return new BeaconConnection.ConnectionCallback() {
      @Override public void onAuthenticated(final BeaconConnection.BeaconCharacteristics beaconChars) {
        runOnUiThread(new Runnable() {
          @Override public void run() {
            statusView.setText("Status: Connected to beacon");
            StringBuilder sb = new StringBuilder()
                .append("Major: ").append(beacon.getMajor()).append("\n")
                .append("Minor: ").append(beacon.getMinor()).append("\n")
                .append("Advertising interval: ").append(beaconChars.getAdvertisingIntervalMillis()).append("ms\n")
                .append("Broadcasting power: ").append(beaconChars.getBroadcastingPower()).append(" dBm\n")
                .append("Battery: ").append(beaconChars.getBatteryPercent()).append(" %");
            beaconDetailsView.setText(sb.toString());
            minorEditView.setText(String.valueOf(beacon.getMinor()));
              majorEditView.setText(String.valueOf(beacon.getMajor()));
              idEditView.setText(String.valueOf(beacon.getProximityUUID()));



            afterConnectedViewMinor.setVisibility(View.VISIBLE);
              afterConnectedViewMajor.setVisibility(View.VISIBLE);
              afterConnectedViewId.setVisibility(View.VISIBLE);
          }
        });
      }

      @Override public void onAuthenticationError() {
        runOnUiThread(new Runnable() {
          @Override public void run() {
            statusView.setText("Status: Cannot connect to beacon. Authentication problems.");
          }
        });
      }

      @Override public void onDisconnected() {
        runOnUiThread(new Runnable() {
          @Override public void run() {
            statusView.setText("Status: Disconnected from beacon");
          }
        });
      }
    };
  }

  private void showToast(String text) {
    Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
  }
}
