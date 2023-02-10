package com.sharkBytesLab.cryptoapp.Fragments

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxInterstitialAd
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sharkBytesLab.cryptoapp.Models.CryptoCurrency
import com.sharkBytesLab.cryptoapp.R
import com.sharkBytesLab.cryptoapp.databinding.FragmentDetailsBinding
import java.util.concurrent.TimeUnit


class DetailsFragment : Fragment() {

    lateinit var binding : FragmentDetailsBinding
    private val item : DetailsFragmentArgs by navArgs()
    private var interstitialAd: MaxInterstitialAd? = null
    private var retry = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDetailsBinding.inflate(layoutInflater)

        val data : CryptoCurrency = item.data!!

        setupDetails(data)
        loadChart(data)
        setButtonOnClick(data)

        addToWatchList(data)
        createInterstitialAd()
        try {
            if (interstitialAd!!.isReady) {
                interstitialAd!!.showAd()
            }
        } catch (e: Exception) {
            Toast.makeText(activity, e.message.toString(), Toast.LENGTH_SHORT).show()
            Log.v("Reset Error", e.message.toString())
        }

        return binding.root
    }

    private fun createInterstitialAd() {
        interstitialAd = MaxInterstitialAd(resources.getString(R.string.applovin_inter_adId), activity)
        val adListener: MaxAdListener = object : MaxAdListener {
            override fun onAdLoaded(ad: MaxAd) {
                Log.e("Reset Error", "Loaded")
            }

            override fun onAdDisplayed(ad: MaxAd) {
                Log.e("Reset Error", "Displayed")
                Toast.makeText(
                    activity,
                    "Ok : " + ad.revenue.toString() + " " + ad.revenuePrecision,
                    Toast.LENGTH_LONG
                ).show()
            }

            override fun onAdHidden(ad: MaxAd) {}
            override fun onAdClicked(ad: MaxAd) {}
            override fun onAdLoadFailed(adUnitId: String, error: MaxError) {
                retry++
                val delay =
                    TimeUnit.SECONDS.toMillis(Math.pow(2.0, Math.min(6, retry).toDouble()).toLong())
                Handler().postDelayed({ interstitialAd!!.loadAd() }, delay)
            }

            override fun onAdDisplayFailed(ad: MaxAd, error: MaxError) {}
        }
        interstitialAd!!.setListener(adListener)
        interstitialAd!!.loadAd()
    }

    var watchList : ArrayList<String>? = null
    var watchListIsChecked = false
    private fun addToWatchList(data: CryptoCurrency)
    {

        readData()

        watchListIsChecked = if(watchList!!.contains(data.symbol))
        {
            binding.addWatchlistButton.setImageResource(R.drawable.ic_star)
            true

        }
        else
        {
            binding.addWatchlistButton.setImageResource(R.drawable.ic_star_outline)
            false
        }

        binding.addWatchlistButton.setOnClickListener{

            watchListIsChecked =  if(!watchListIsChecked)
            {
                if(!watchList!!.contains(data.symbol)){
                    watchList!!.add(data.symbol)
                }
                storeData()
                binding.addWatchlistButton.setImageResource(R.drawable.ic_star)
                true
            }else
            {
                binding.addWatchlistButton.setImageResource(R.drawable.ic_star_outline)
                watchList!!.remove(data.symbol)
                storeData()
                false
            }
        }

    }

    private fun storeData()
    {
        val sharedPreferences = requireContext().getSharedPreferences("watchlist", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(watchList)
        editor.putString("watchlist", json)
        editor.apply()
    }

    private fun readData()
    {

        val sharedPreference = requireContext().getSharedPreferences("watchlist",Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreference.getString("watchlist", ArrayList<String>().toString())
        val type = object : TypeToken<ArrayList<String>>(){}.type
        watchList = gson.fromJson(json, type)

    }

    private fun setButtonOnClick(item: CryptoCurrency)
    {

        val oneMonth = binding.button
        val oneWeek = binding.button1
        val oneDay = binding.button2
        val fourHour = binding.button3
        val oneHour = binding.button4
        val fifteenMinute = binding.button5

        val clickListener = View.OnClickListener {
            when(it.id){
                fifteenMinute.id -> loadChartData(it,"15",item, oneDay, oneMonth, oneWeek, fourHour, oneHour)
                oneHour.id -> loadChartData(it,"1H",item, oneDay, oneMonth, oneWeek, fourHour, fifteenMinute)
                fourHour.id -> loadChartData(it,"4H",item, oneDay, oneMonth, oneWeek, fifteenMinute, oneHour)
                oneDay.id -> loadChartData(it,"D",item, fifteenMinute, oneMonth, oneWeek, fourHour, oneHour)
                oneWeek.id -> loadChartData(it,"W",item, oneDay, oneMonth, fifteenMinute, fourHour, oneHour)
                oneMonth.id -> loadChartData(it,"M",item, oneDay, fifteenMinute, oneWeek, fourHour, oneHour)
            }

        }

        fifteenMinute.setOnClickListener(clickListener)
        oneHour.setOnClickListener(clickListener)
        fourHour.setOnClickListener(clickListener)
        oneDay.setOnClickListener(clickListener)
        oneWeek.setOnClickListener(clickListener)
        oneMonth.setOnClickListener(clickListener)

    }

    private fun loadChartData(
        it: View?,
        s: String,
        item: CryptoCurrency,
        oneDay: AppCompatButton,
        oneMonth: AppCompatButton,
        oneWeek: AppCompatButton,
        fourHour: AppCompatButton,
        oneHour: AppCompatButton
    ) {

        disableButton(oneDay, oneMonth, oneWeek, fourHour, oneHour)
        it!!.setBackgroundResource(R.drawable.active_button)
        binding.detaillChartWebView.settings.javaScriptEnabled = true
        binding.detaillChartWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        binding.detaillChartWebView.loadUrl("https://s.tradingview.com/widgetembed/?frameElementId=tradingview_76d87&symbol="+item.symbol.toString() + "USD&interval="+s+"&hidesidetoolbar=1&hidetoptoolbar=1&symboledit=1&saveimage=1&toolbarbg=F1F3F6&studies=[]&hideideas=1&theme=Dark&style=1&timezone=Etc%2FUTC&studies_overrides={}&overrides={}&enabled_features=[]&disabled_features=[]&locale=en&utm=source=coinmarketcap.com&utm_medium=widget&utm_campaign=chart&utm_term=BTCUSDT")


    }

    private fun disableButton(oneDay: AppCompatButton, oneMonth: AppCompatButton, oneWeek: AppCompatButton, fourHour: AppCompatButton, oneHour: AppCompatButton)
    {

        oneDay.background = null
        oneHour.background = null
        oneMonth.background = null
        oneWeek.background = null
        fourHour.background = null

    }


    private fun loadChart(item: CryptoCurrency)
    {

        binding.detaillChartWebView.settings.javaScriptEnabled = true
        binding.detaillChartWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        Log.d("Itemno", item.symbol.toString())
        binding.detaillChartWebView.loadUrl( "https://s.tradingview.com/widgetembed/?frameElementId=tradingview_76d87&symbol="+ item.symbol.toString() + "USD&interval=D&hidesidetoolbar=1&hidetoptoolbar=1&symboledit=1&saveimage=1&toolbarbg="+"F1F3F6&studies=[]&hideideas=1&theme=Dark&style=1&timezone=Etc%2FUTC&studies_overrides={}&overrides={}&enabled_features=[]&disabled_features=[]&locale=en&utm=source=coinmarketcap.com&utm_medium=widget&utm_campaign=chart&utm_term=BTCUSDT")

    }


    private fun setupDetails(data: CryptoCurrency)
    {

        binding.detailSymbolTextView.text = data.symbol

        Glide.with(requireContext()).load("https://s2.coinmarketcap.com/static/img/coins/64x64/"+ data.id + ".png").thumbnail(
            Glide.with(requireContext()).load(R.drawable.spinner)).into(binding.detailImageView)

        binding.detailPriceTextView.text = "${String.format("$%.4f", data.quotes[0].price)}"

        if(data.quotes!![0].percentChange24h > 0)
        {
            binding.detailChangeTextView.setTextColor(requireContext().resources.getColor(R.color.green))
            binding.detailChangeImageView.setImageResource(R.drawable.ic_caret_up)
            binding.detailChangeTextView.text = "+${String.format("%.02f", data.quotes[0].percentChange24h)} %"
        }else
        {
            binding.detailChangeTextView.setTextColor(requireContext().resources.getColor(R.color.red))
            binding.detailChangeImageView.setImageResource(R.drawable.ic_caret_down)
            binding.detailChangeTextView.text = "${String.format("%.02f", data.quotes[0].percentChange24h)} %"
        }

    }


}