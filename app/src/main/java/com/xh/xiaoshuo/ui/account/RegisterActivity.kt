package com.xh.xiaoshuo.ui.account

import android.content.Intent
import android.os.CountDownTimer
import android.text.Editable
import com.qy.reader.common.utils.StringUtils
import com.xh.common.base.BaseActivity
import com.xh.common.ktextended.addTextChangedListener
import com.xh.common.ktextended.showToast
import com.xh.common.model.impl.InputTextWatcherModel
import com.xh.xiaoshuo.BuildConfig
import com.xh.xiaoshuo.R
import com.xh.xiaoshuo.bean.RegisterReqBean
import com.xh.xiaoshuo.mode.MOBCode
import com.xh.xiaoshuo.network.api.UserApi
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.coroutines.launch


class RegisterActivity : BaseActivity() {


    private val mobCode by lazy {
        MOBCode({
            if (it) {
                timer.start()
            } else {
                timer.cancel()
                btn_get_code.isEnabled = true
                btn_get_code.setText("获取验证码")
            }
            mLoadingDialog.dismiss()
        }, {

            if (it) {
                mMainScope.launch {
                    mLoadingDialog.show()
                    val data = UserApi().register(RegisterReqBean(et_phone.text.toString(),
                            et_vf.text.toString(),
                            et_password.text.toString(), et_invitation_code.text.toString()))
                    if (!StringUtils.isEmpty(data)) {
                        "注册成功".showToast()
                        startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                        finish()
                    }
                    mLoadingDialog.dismiss()
                }


            } else {
                "验证码错误，请重新输入".showToast()
                mLoadingDialog.dismiss()
            }
        })
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_register
    }

    override fun initView() {

        et_phone.addTextChangedListener {
            btn_register.isEnabled = isBtnRegisterEnabled()

        }

        et_vf.addTextChangedListener {
            btn_register.isEnabled = isBtnRegisterEnabled()
        }
        et_password.addTextChangedListener(object : InputTextWatcherModel(et_password) {
            override fun afterTextChanged(editable: Editable) {
                super.afterTextChanged(editable)
                btn_register.isEnabled = isBtnRegisterEnabled()
            }
        })
        btn_get_code.setOnClickListener {
            if (et_phone.text.toString().length == 11) {
                getCode()
            } else {
                "请输入正确的手机号码".showToast()
            }
        }
        btn_register.setOnClickListener {

            if (BuildConfig.DEBUG) {
                mMainScope.launch {
                    mLoadingDialog.show()
                    val data = UserApi().register(RegisterReqBean(et_phone.text.toString(),
                            et_vf.text.toString(),
                            et_password.text.toString(), et_invitation_code.text.toString()))
                    if (!StringUtils.isEmpty(data)) {
                        "注册成功".showToast()
                    }
                    mLoadingDialog.dismiss()
                }



                return@setOnClickListener
            }
            mLoadingDialog.show()
            mobCode.vfCode(et_phone.text.toString(), et_vf.text.toString())
//            UserApi().register(RegisterReqBean(et_phone.text.toString(),
//                    et_vf.text.toString(),
//                    et_password.text.toString()), bindToLifecycle(), {
//                success {
//                    "注册成功".showToast()
//                    startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
//                    finish()
//                }
//            })

        }

    }


    private fun isBtnRegisterEnabled(): Boolean {
        return et_phone.text.toString().length == 11
                && et_password.text.toString().length >= 6
                && et_vf.text.toString().length >= 4
    }


    private fun getCode() {
        mLoadingDialog.show()
        mobCode.getCode(et_phone.text.toString())

//        UserApi().getVfCode(VfCodeReqBean(et_phone.text.toString(), 1), bindToLifecycle(), {
//            success {
//                timer.start()
//            }
//            fail {
//                timer.cancel()
//                btn_get_code.isEnabled = true
//                btn_get_code.setText("获取验证码")
//            }
//        })


    }

    private val timer = object : CountDownTimer(60 * 1000, 1000) {
        override fun onFinish() {
            btn_get_code.isEnabled = true
            btn_get_code.setText("重发")
        }

        override fun onTick(millisUntilFinished: Long) {
            btn_get_code.setText("重发(${(millisUntilFinished.toInt() / 1000)}s)")
            btn_get_code.isEnabled = false
        }

    }


    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
        mobCode.onDestory()
    }

//    private fun isPhone(): Boolean {
//        return RegexUtils.isMobileExact(etPhone.text.toString())
//    }
}