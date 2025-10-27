package com.example.simplespeedview

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.simplespeedview.databinding.ActivityMainBinding
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        // 加速、减速、归零按钮
        binding.button.setOnClickListener {
            val newSpeed = binding.simpleSpeedView.getSpeed() + 10
            binding.simpleSpeedView.animateToSpeed(newSpeed) // 使用动画
        }
        binding.button2.setOnClickListener {
            val newSpeed = binding.simpleSpeedView.getSpeed() - 10
            binding.simpleSpeedView.animateToSpeed(newSpeed) // 使用动画
        }
        binding.button3.setOnClickListener {
            binding.simpleSpeedView.animateToSpeed(0f) // 使用动画
        }

        // 速度设置
        binding.seekBarSpeed.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.tvSpeedValue.text = "$progress km/h"
                if (fromUser) {
                    binding.simpleSpeedView.animateToSpeed(progress.toFloat(), 200)
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // 最大速度设置
        binding.seekBarMaxSpeed.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.tvMaxSpeed.text = progress.toString()
                if (fromUser) {
                    binding.simpleSpeedView.setMaxSpeed(progress.toFloat())
                    binding.seekBarSpeed.max = progress
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // 圆环宽度
        binding.seekBarArcWidth.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.tvArcWidth.text = progress.toString()
                if (fromUser) {
                    binding.simpleSpeedView.setArcWidth(progress.toFloat())
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // 指针长度
        binding.seekBarPointerLength.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.tvPointerLength.text = progress.toString()
                if (fromUser) {
                    binding.simpleSpeedView.setPointerLength(progress.toFloat())
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // 指针宽度
        binding.seekBarPointerWidth.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.tvPointerWidth.text = progress.toString()
                if (fromUser) {
                    binding.simpleSpeedView.setPointerWidth(progress.toFloat())
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // 指针样式 - 颜色指针
        binding.btnPointerStyleColor.setOnClickListener {
            binding.simpleSpeedView.setPointerDrawable(0) // 0表示使用颜色绘制
        }

        // 指针样式 - 图片指针（bg_05）
        binding.btnPointerStyleImage.setOnClickListener {
            binding.simpleSpeedView.setPointerDrawable(R.drawable.bg_05)
        }

        // 速度文本大小
        binding.seekBarSpeedTextSize.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.tvSpeedTextSize.text = progress.toString()
                if (fromUser) {
                    binding.simpleSpeedView.setSpeedTextSize(progress.toFloat())
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // 刻度文本大小
        binding.seekBarScaleTextSize.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.tvScaleTextSize.text = progress.toString()
                if (fromUser) {
                    binding.simpleSpeedView.setScaleTextSize(progress.toFloat())
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // 刻度数量
        binding.seekBarScaleCount.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.tvScaleCount.text = progress.toString()
                if (fromUser) {
                    binding.simpleSpeedView.setScaleCount(progress)
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // 颜色按钮
        binding.btnProgressColor.setOnClickListener {
            val randomColor = getRandomColor()
            binding.simpleSpeedView.setArcProgressColor(randomColor)
        }

        binding.btnPointerColor.setOnClickListener {
            val randomColor = getRandomColor()
            binding.simpleSpeedView.setPointerColor(randomColor)
        }

        binding.btnSpeedTextColor.setOnClickListener {
            val randomColor = getRandomColor()
            binding.simpleSpeedView.setSpeedTextColor(randomColor)
        }

        binding.btnScaleTextColor.setOnClickListener {
            val randomColor = getRandomColor()
            binding.simpleSpeedView.setScaleTextColor(randomColor)
        }

        // 显示开关
        binding.cbShowSpeedText.setOnCheckedChangeListener { _, isChecked ->
            binding.simpleSpeedView.setShowSpeedText(isChecked)
        }

        binding.cbShowScaleText.setOnCheckedChangeListener { _, isChecked ->
            binding.simpleSpeedView.setShowScaleText(isChecked)
        }

        binding.cbScaleTextOutside.setOnCheckedChangeListener { _, isChecked ->
            binding.simpleSpeedView.setScaleTextOutside(isChecked)
        }

        // 起始角度
        binding.seekBarStartAngle.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.tvStartAngle.text = "起始: ${progress}°"
                if (fromUser) {
                    binding.simpleSpeedView.setStartAngle(progress.toFloat())
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // 扫过角度
        binding.seekBarSweepAngle.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.tvSweepAngle.text = "扫过: ${progress}°"
                if (fromUser) {
                    binding.simpleSpeedView.setSweepAngle(progress.toFloat())
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // 圆环边框宽度
        binding.seekBarArcBorderWidth.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.tvArcBorderWidth.text = "宽度: $progress"
                if (fromUser) {
                    binding.simpleSpeedView.setArcBorderWidth(progress.toFloat())
                    // 如果设置了边框宽度但边框是透明的，自动设置一个可见颜色
                    if (progress > 0) {
                        binding.simpleSpeedView.setArcBorderColor(Color.BLACK)
                    }
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // 指针偏移量
        binding.seekBarPointerOffset.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.tvPointerOffset.text = progress.toString()
                if (fromUser) {
                    binding.simpleSpeedView.setPointerOffset(progress.toFloat())
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // 速度单位按钮
        binding.btnUnitKmh.setOnClickListener {
            binding.simpleSpeedView.setSpeedUnit("km/h")
        }
        binding.btnUnitMph.setOnClickListener {
            binding.simpleSpeedView.setSpeedUnit("mph")
        }
        binding.btnUnitMs.setOnClickListener {
            binding.simpleSpeedView.setSpeedUnit("m/s")
        }

        // 速度单位大小
        binding.seekBarSpeedUnitSize.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.tvSpeedUnitSize.text = "单位大小: $progress"
                if (fromUser) {
                    binding.simpleSpeedView.setSpeedUnitSize(progress.toFloat())
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // 速度文本垂直偏移
        binding.seekBarSpeedTextOffsetY.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.tvSpeedTextOffsetY.text = progress.toString()
                if (fromUser) {
                    binding.simpleSpeedView.setSpeedTextOffsetY(progress.toFloat())
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // 速度文本背景选择
        binding.btnBgNone.setOnClickListener {
            binding.simpleSpeedView.setSpeedTextBackground(0) // 无背景
        }
        binding.btnBgCircle.setOnClickListener {
            binding.simpleSpeedView.setSpeedTextBackground(R.drawable.bg_speed_circle) // 圆形背景
        }
        binding.btnBgRounded.setOnClickListener {
            binding.simpleSpeedView.setSpeedTextBackground(R.drawable.bg_speed_rounded) // 圆角背景
        }

        // 速度文本背景内边距
        binding.seekBarSpeedTextPadding.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.tvSpeedTextPadding.text = "背景边距: $progress"
                if (fromUser) {
                    binding.simpleSpeedView.setSpeedTextBackgroundPadding(progress.toFloat())
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // 刻度样式按钮
        binding.btnScaleStyleNormal.setOnClickListener {
            binding.simpleSpeedView.setScaleTextStyle(0) // normal
        }
        binding.btnScaleStyleBold.setOnClickListener {
            binding.simpleSpeedView.setScaleTextStyle(1) // bold
        }
        binding.btnScaleStyleItalic.setOnClickListener {
            binding.simpleSpeedView.setScaleTextStyle(2) // italic
        }
        binding.btnScaleStyleBoldItalic.setOnClickListener {
            binding.simpleSpeedView.setScaleTextStyle(3) // bold_italic
        }

        // 更多颜色按钮
        binding.btnArcBackgroundColor.setOnClickListener {
            val randomColor = getRandomColor()
            binding.simpleSpeedView.setArcBackgroundColor(randomColor)
        }

        binding.btnArcBorderColor.setOnClickListener {
            val randomColor = getRandomColor()
            binding.simpleSpeedView.setArcBorderColor(randomColor)
            // 如果边框宽度为0，自动设置一个默认宽度
            if (binding.seekBarArcBorderWidth.progress == 0) {
                binding.seekBarArcBorderWidth.progress = 3
                binding.simpleSpeedView.setArcBorderWidth(3f)
            }
        }

        binding.btnSpeedUnitColor.setOnClickListener {
            val randomColor = getRandomColor()
            binding.simpleSpeedView.setSpeedUnitColor(randomColor)
        }

        // 重置所有颜色为默认值
        binding.btnResetColors.setOnClickListener {
            binding.simpleSpeedView.setArcBackgroundColor(Color.LTGRAY)
            binding.simpleSpeedView.setArcProgressColor(Color.parseColor("#00BCD4"))
            binding.simpleSpeedView.setPointerColor(Color.RED)
            binding.simpleSpeedView.setSpeedTextColor(Color.BLACK)
            binding.simpleSpeedView.setSpeedUnitColor(Color.GRAY)
            binding.simpleSpeedView.setScaleTextColor(Color.GRAY)
            binding.simpleSpeedView.setArcBorderColor(Color.TRANSPARENT)
            // 重置边框宽度
            binding.seekBarArcBorderWidth.progress = 0
            binding.simpleSpeedView.setArcBorderWidth(0f)
        }
    }

    /**
     * 生成随机颜色
     */
    private fun getRandomColor(): Int {
        val colors = arrayOf(
            Color.parseColor("#FF5722"), // 深橙色
            Color.parseColor("#2196F3"), // 蓝色
            Color.parseColor("#4CAF50"), // 绿色
            Color.parseColor("#9C27B0"), // 紫色
            Color.parseColor("#FF9800"), // 橙色
            Color.parseColor("#00BCD4"), // 青色
            Color.parseColor("#E91E63"), // 粉红色
            Color.parseColor("#FFC107"), // 琥珀色
            Color.parseColor("#795548"), // 棕色
            Color.parseColor("#607D8B")  // 蓝灰色
        )
        return colors[Random.nextInt(colors.size)]
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.github -> {
                try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/DIABLOSER/SimpleSpeedView"));
                    startActivity(intent);
                } catch (e: ActivityNotFoundException) {
                    // 处理没有浏览器的情况
                    Toast.makeText(this, "未找到浏览器应用", Toast.LENGTH_SHORT).show();
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}