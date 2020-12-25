#if !defined(_MATHUTIL_HH)
#define _MATHUTIL_HH

inline double map(double val, double inLow, double inHigh, double outLow, double outHigh){
    return (val - inLow) * (outHigh - outLow) / (inHigh - inLow) + outLow;
}

// #define map(val, inLow, inHigh, outLow, outHigh) \
//     (val - inLow) * (outHigh - outLow) / (inHigh - inLow) + outLow

#endif  // _MATHUTIL_HH
