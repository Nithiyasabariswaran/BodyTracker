// Restrukturert: ok

package no.slomic.body.measurements.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import no.slomic.body.measurements.entities.Measurement;
import no.slomic.body.measurements.entities.Quantity;
import no.slomic.body.measurements.holders.MeasurementHolder;
import no.slomic.body.measurements.utils.DateUtils;
import no.slomic.body.measurements.utils.QuantityStringFormat;

import java.util.List;

public class WeightMeasurementAdapter extends MeasurementAdapter {
    private Context mContext;
    private int mLayoutResourceId;

    /**
     * @param Context
     * @param LayoutResourceId
     * @param measurements
     */
    public WeightMeasurementAdapter(Context context, int layoutResourceId,
            List<Measurement> measurements) {
        super(context, layoutResourceId, measurements);
        this.mLayoutResourceId = layoutResourceId;
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        MeasurementHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(mLayoutResourceId, parent, false);

            holder = new MeasurementHolder(row);
            row.setTag(holder);
        } else {
            holder = (MeasurementHolder) row.getTag();
        }

        Measurement measurement = mMeasurements.get(position);
        bindDataToViews(holder, measurement);

        return row;
    }

    public void bindDataToViews(MeasurementHolder holder, Measurement measurement) {
        // Set measurement date
        String date = DateUtils.formatToMediumFormatExtended(measurement.getDate());
        holder.getMeasurementDate().setText(date);

        // Set measurement value in preferred system of measurement
        String formattedValue = getFormattedQuantityValue(measurement.getQuantity());
        holder.getMeasurementValue().setText(formattedValue);

        // Set diff value between this and previous measurement
        if (measurement.getPrevious() != null) {
            Quantity diff = measurement.getQuantity().subtract(
                    measurement.getPrevious().getQuantity(),
                    measurement.getQuantity().getUnit().getSystemUnit());

            String formattedDiffValue = getFormattedQuantityValue(diff);
            holder.getDiffValue().setText(formattedDiffValue);

            // Set diff relational sign/icon
            if (diff.getValue() < 0)
                holder.getDiffIcon().setImageBitmap(mDownIcon);
            else if (diff.getValue() == 0)
                holder.getDiffIcon().setImageBitmap(mEqualIcon);
            else
                holder.getDiffIcon().setImageBitmap(mUpIcon);
        } else {
            holder.getDiffValue().setText("");
            holder.getDiffIcon().setImageDrawable(null);
        }
    }

    public String getFormattedQuantityValue(Quantity q) {
        if (mSystemOfMeasurement.equals(mMetricUnits))
            return QuantityStringFormat.formatWeightToMetric(q);
        else if (mSystemOfMeasurement.equals(mImperialUnits))
            return QuantityStringFormat.formatWeightToImperial(q);
        else
            return "";
    }
}