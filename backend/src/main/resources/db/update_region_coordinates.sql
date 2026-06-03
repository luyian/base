-- ----------------------------
-- sys_region 地级市经纬度更新 SQL
-- 数据来源：高德开放平台行政区划坐标
-- 匹配条件：region_code（4位，去掉末尾00）
-- ----------------------------

-- 直辖市（2位编码）
UPDATE sys_region SET longitude = 116.407, latitude = 39.904 WHERE region_code = '11';
UPDATE sys_region SET longitude = 117.190, latitude = 39.126 WHERE region_code = '12';
UPDATE sys_region SET longitude = 121.473, latitude = 31.230 WHERE region_code = '31';
UPDATE sys_region SET longitude = 106.504, latitude = 29.533 WHERE region_code = '50';

-- 河北省
UPDATE sys_region SET longitude = 114.502, latitude = 38.045 WHERE region_code = '1301';
UPDATE sys_region SET longitude = 118.175, latitude = 39.635 WHERE region_code = '1302';
UPDATE sys_region SET longitude = 119.586, latitude = 39.942 WHERE region_code = '1303';
UPDATE sys_region SET longitude = 114.490, latitude = 36.612 WHERE region_code = '1304';
UPDATE sys_region SET longitude = 114.508, latitude = 37.068 WHERE region_code = '1305';
UPDATE sys_region SET longitude = 115.482, latitude = 38.867 WHERE region_code = '1306';
UPDATE sys_region SET longitude = 114.884, latitude = 40.824 WHERE region_code = '1307';
UPDATE sys_region SET longitude = 117.939, latitude = 40.976 WHERE region_code = '1308';
UPDATE sys_region SET longitude = 116.857, latitude = 38.311 WHERE region_code = '1309';
UPDATE sys_region SET longitude = 116.704, latitude = 39.523 WHERE region_code = '1310';
UPDATE sys_region SET longitude = 115.665, latitude = 37.735 WHERE region_code = '1311';

-- 山西省
UPDATE sys_region SET longitude = 112.549, latitude = 37.857 WHERE region_code = '1401';
UPDATE sys_region SET longitude = 113.295, latitude = 40.090 WHERE region_code = '1402';
UPDATE sys_region SET longitude = 113.583, latitude = 37.861 WHERE region_code = '1403';
UPDATE sys_region SET longitude = 113.114, latitude = 36.191 WHERE region_code = '1404';
UPDATE sys_region SET longitude = 112.851, latitude = 35.497 WHERE region_code = '1405';
UPDATE sys_region SET longitude = 112.433, latitude = 39.331 WHERE region_code = '1406';
UPDATE sys_region SET longitude = 112.736, latitude = 37.696 WHERE region_code = '1407';
UPDATE sys_region SET longitude = 111.004, latitude = 35.022 WHERE region_code = '1408';
UPDATE sys_region SET longitude = 112.734, latitude = 38.417 WHERE region_code = '1409';
UPDATE sys_region SET longitude = 111.517, latitude = 36.084 WHERE region_code = '1410';
UPDATE sys_region SET longitude = 111.134, latitude = 37.524 WHERE region_code = '1411';

-- 内蒙古
UPDATE sys_region SET longitude = 111.671, latitude = 40.818 WHERE region_code = '1501';
UPDATE sys_region SET longitude = 109.840, latitude = 40.658 WHERE region_code = '1502';
UPDATE sys_region SET longitude = 106.825, latitude = 39.673 WHERE region_code = '1503';
UPDATE sys_region SET longitude = 118.956, latitude = 42.275 WHERE region_code = '1504';
UPDATE sys_region SET longitude = 122.263, latitude = 43.617 WHERE region_code = '1505';
UPDATE sys_region SET longitude = 109.990, latitude = 39.817 WHERE region_code = '1506';
UPDATE sys_region SET longitude = 119.758, latitude = 49.215 WHERE region_code = '1507';
UPDATE sys_region SET longitude = 107.416, latitude = 40.757 WHERE region_code = '1508';
UPDATE sys_region SET longitude = 113.114, latitude = 40.994 WHERE region_code = '1509';
UPDATE sys_region SET longitude = 122.070, latitude = 44.566 WHERE region_code = '1522';
UPDATE sys_region SET longitude = 116.090, latitude = 43.944 WHERE region_code = '1525';
UPDATE sys_region SET longitude = 111.965, latitude = 41.034 WHERE region_code = '1529';

-- 辽宁省
UPDATE sys_region SET longitude = 123.429, latitude = 41.796 WHERE region_code = '2101';
UPDATE sys_region SET longitude = 121.618, latitude = 38.914 WHERE region_code = '2102';
UPDATE sys_region SET longitude = 122.995, latitude = 41.110 WHERE region_code = '2103';
UPDATE sys_region SET longitude = 123.921, latitude = 41.876 WHERE region_code = '2104';
UPDATE sys_region SET longitude = 123.770, latitude = 41.297 WHERE region_code = '2105';
UPDATE sys_region SET longitude = 124.383, latitude = 40.124 WHERE region_code = '2106';
UPDATE sys_region SET longitude = 121.135, latitude = 41.119 WHERE region_code = '2107';
UPDATE sys_region SET longitude = 122.235, latitude = 40.667 WHERE region_code = '2108';
UPDATE sys_region SET longitude = 121.648, latitude = 42.012 WHERE region_code = '2109';
UPDATE sys_region SET longitude = 123.238, latitude = 41.268 WHERE region_code = '2110';
UPDATE sys_region SET longitude = 122.070, latitude = 41.124 WHERE region_code = '2111';
UPDATE sys_region SET longitude = 123.844, latitude = 42.290 WHERE region_code = '2112';
UPDATE sys_region SET longitude = 120.451, latitude = 41.576 WHERE region_code = '2113';
UPDATE sys_region SET longitude = 120.856, latitude = 40.755 WHERE region_code = '2114';

-- 吉林省
UPDATE sys_region SET longitude = 125.324, latitude = 43.886 WHERE region_code = '2201';
UPDATE sys_region SET longitude = 126.553, latitude = 43.843 WHERE region_code = '2202';
UPDATE sys_region SET longitude = 124.370, latitude = 43.170 WHERE region_code = '2203';
UPDATE sys_region SET longitude = 125.145, latitude = 42.902 WHERE region_code = '2204';
UPDATE sys_region SET longitude = 125.936, latitude = 41.721 WHERE region_code = '2205';
UPDATE sys_region SET longitude = 126.427, latitude = 41.942 WHERE region_code = '2206';
UPDATE sys_region SET longitude = 124.823, latitude = 45.118 WHERE region_code = '2207';
UPDATE sys_region SET longitude = 122.841, latitude = 45.619 WHERE region_code = '2208';
UPDATE sys_region SET longitude = 129.513, latitude = 42.891 WHERE region_code = '2224';
UPDATE sys_region SET longitude = 126.714, latitude = 44.221 WHERE region_code = '2225';

-- 黑龙江省
UPDATE sys_region SET longitude = 126.642, latitude = 45.757 WHERE region_code = '2301';
UPDATE sys_region SET longitude = 123.958, latitude = 47.342 WHERE region_code = '2302';
UPDATE sys_region SET longitude = 130.970, latitude = 45.300 WHERE region_code = '2303';
UPDATE sys_region SET longitude = 130.277, latitude = 47.332 WHERE region_code = '2304';
UPDATE sys_region SET longitude = 131.157, latitude = 46.643 WHERE region_code = '2305';
UPDATE sys_region SET longitude = 125.113, latitude = 46.590 WHERE region_code = '2306';
UPDATE sys_region SET longitude = 128.899, latitude = 47.724 WHERE region_code = '2307';
UPDATE sys_region SET longitude = 130.361, latitude = 46.809 WHERE region_code = '2308';
UPDATE sys_region SET longitude = 131.015, latitude = 45.771 WHERE region_code = '2309';
UPDATE sys_region SET longitude = 129.618, latitude = 44.583 WHERE region_code = '2310';
UPDATE sys_region SET longitude = 127.499, latitude = 50.249 WHERE region_code = '2311';
UPDATE sys_region SET longitude = 126.993, latitude = 46.637 WHERE region_code = '2312';
UPDATE sys_region SET longitude = 124.872, latitude = 46.590 WHERE region_code = '2327';

-- 江苏省
UPDATE sys_region SET longitude = 118.767, latitude = 32.041 WHERE region_code = '3201';
UPDATE sys_region SET longitude = 120.301, latitude = 31.574 WHERE region_code = '3202';
UPDATE sys_region SET longitude = 117.184, latitude = 34.261 WHERE region_code = '3203';
UPDATE sys_region SET longitude = 119.946, latitude = 31.772 WHERE region_code = '3204';
UPDATE sys_region SET longitude = 120.619, latitude = 31.299 WHERE region_code = '3205';
UPDATE sys_region SET longitude = 120.864, latitude = 32.016 WHERE region_code = '3206';
UPDATE sys_region SET longitude = 119.178, latitude = 34.600 WHERE region_code = '3207';
UPDATE sys_region SET longitude = 119.021, latitude = 33.597 WHERE region_code = '3208';
UPDATE sys_region SET longitude = 120.139, latitude = 33.377 WHERE region_code = '3209';
UPDATE sys_region SET longitude = 119.421, latitude = 32.393 WHERE region_code = '3210';
UPDATE sys_region SET longitude = 119.452, latitude = 32.204 WHERE region_code = '3211';
UPDATE sys_region SET longitude = 119.915, latitude = 32.484 WHERE region_code = '3212';
UPDATE sys_region SET longitude = 118.275, latitude = 33.963 WHERE region_code = '3213';

-- 浙江省
UPDATE sys_region SET longitude = 120.153, latitude = 30.287 WHERE region_code = '3301';
UPDATE sys_region SET longitude = 121.549, latitude = 29.868 WHERE region_code = '3302';
UPDATE sys_region SET longitude = 120.672, latitude = 28.000 WHERE region_code = '3303';
UPDATE sys_region SET longitude = 120.750, latitude = 30.762 WHERE region_code = '3304';
UPDATE sys_region SET longitude = 120.102, latitude = 30.867 WHERE region_code = '3305';
UPDATE sys_region SET longitude = 120.582, latitude = 30.036 WHERE region_code = '3306';
UPDATE sys_region SET longitude = 119.649, latitude = 29.089 WHERE region_code = '3307';
UPDATE sys_region SET longitude = 118.872, latitude = 28.941 WHERE region_code = '3308';
UPDATE sys_region SET longitude = 122.106, latitude = 30.016 WHERE region_code = '3309';
UPDATE sys_region SET longitude = 121.428, latitude = 28.661 WHERE region_code = '3310';
UPDATE sys_region SET longitude = 119.921, latitude = 28.451 WHERE region_code = '3311';

-- 安徽省
UPDATE sys_region SET longitude = 117.283, latitude = 31.861 WHERE region_code = '3401';
UPDATE sys_region SET longitude = 118.376, latitude = 31.326 WHERE region_code = '3402';
UPDATE sys_region SET longitude = 117.363, latitude = 32.939 WHERE region_code = '3403';
UPDATE sys_region SET longitude = 117.018, latitude = 32.647 WHERE region_code = '3404';
UPDATE sys_region SET longitude = 118.507, latitude = 31.689 WHERE region_code = '3405';
UPDATE sys_region SET longitude = 116.794, latitude = 33.971 WHERE region_code = '3406';
UPDATE sys_region SET longitude = 117.816, latitude = 30.929 WHERE region_code = '3407';
UPDATE sys_region SET longitude = 117.043, latitude = 30.508 WHERE region_code = '3408';
UPDATE sys_region SET longitude = 118.317, latitude = 29.709 WHERE region_code = '3410';
UPDATE sys_region SET longitude = 118.316, latitude = 32.303 WHERE region_code = '3411';
UPDATE sys_region SET longitude = 115.819, latitude = 32.896 WHERE region_code = '3412';
UPDATE sys_region SET longitude = 116.984, latitude = 33.633 WHERE region_code = '3413';
UPDATE sys_region SET longitude = 116.507, latitude = 31.752 WHERE region_code = '3415';
UPDATE sys_region SET longitude = 115.783, latitude = 33.869 WHERE region_code = '3416';
UPDATE sys_region SET longitude = 117.489, latitude = 30.656 WHERE region_code = '3417';
UPDATE sys_region SET longitude = 118.757, latitude = 30.940 WHERE region_code = '3418';

-- 福建省
UPDATE sys_region SET longitude = 119.306, latitude = 26.075 WHERE region_code = '3501';
UPDATE sys_region SET longitude = 118.100, latitude = 24.479 WHERE region_code = '3502';
UPDATE sys_region SET longitude = 119.007, latitude = 25.431 WHERE region_code = '3503';
UPDATE sys_region SET longitude = 117.635, latitude = 26.265 WHERE region_code = '3504';
UPDATE sys_region SET longitude = 118.589, latitude = 24.908 WHERE region_code = '3505';
UPDATE sys_region SET longitude = 117.661, latitude = 24.510 WHERE region_code = '3506';
UPDATE sys_region SET longitude = 118.178, latitude = 26.635 WHERE region_code = '3507';
UPDATE sys_region SET longitude = 117.030, latitude = 25.091 WHERE region_code = '3508';
UPDATE sys_region SET longitude = 119.527, latitude = 26.659 WHERE region_code = '3509';

-- 江西省
UPDATE sys_region SET longitude = 115.892, latitude = 28.676 WHERE region_code = '3601';
UPDATE sys_region SET longitude = 117.214, latitude = 29.292 WHERE region_code = '3602';
UPDATE sys_region SET longitude = 113.852, latitude = 27.622 WHERE region_code = '3603';
UPDATE sys_region SET longitude = 115.992, latitude = 29.712 WHERE region_code = '3604';
UPDATE sys_region SET longitude = 114.930, latitude = 27.810 WHERE region_code = '3605';
UPDATE sys_region SET longitude = 117.034, latitude = 28.238 WHERE region_code = '3606';
UPDATE sys_region SET longitude = 114.940, latitude = 25.850 WHERE region_code = '3607';
UPDATE sys_region SET longitude = 114.986, latitude = 27.111 WHERE region_code = '3608';
UPDATE sys_region SET longitude = 114.391, latitude = 27.804 WHERE region_code = '3609';
UPDATE sys_region SET longitude = 116.358, latitude = 27.948 WHERE region_code = '3610';
UPDATE sys_region SET longitude = 117.971, latitude = 28.454 WHERE region_code = '3611';

-- 山东省
UPDATE sys_region SET longitude = 117.000, latitude = 36.675 WHERE region_code = '3701';
UPDATE sys_region SET longitude = 120.355, latitude = 36.083 WHERE region_code = '3702';
UPDATE sys_region SET longitude = 118.047, latitude = 36.814 WHERE region_code = '3703';
UPDATE sys_region SET longitude = 117.557, latitude = 34.856 WHERE region_code = '3704';
UPDATE sys_region SET longitude = 118.666, latitude = 37.434 WHERE region_code = '3705';
UPDATE sys_region SET longitude = 121.390, latitude = 37.539 WHERE region_code = '3706';
UPDATE sys_region SET longitude = 119.107, latitude = 36.709 WHERE region_code = '3707';
UPDATE sys_region SET longitude = 116.587, latitude = 35.415 WHERE region_code = '3708';
UPDATE sys_region SET longitude = 117.129, latitude = 36.195 WHERE region_code = '3709';
UPDATE sys_region SET longitude = 122.116, latitude = 37.509 WHERE region_code = '3710';
UPDATE sys_region SET longitude = 119.461, latitude = 35.428 WHERE region_code = '3711';
UPDATE sys_region SET longitude = 118.326, latitude = 35.065 WHERE region_code = '3713';
UPDATE sys_region SET longitude = 116.307, latitude = 37.453 WHERE region_code = '3714';
UPDATE sys_region SET longitude = 115.980, latitude = 36.456 WHERE region_code = '3715';
UPDATE sys_region SET longitude = 118.016, latitude = 37.383 WHERE region_code = '3716';
UPDATE sys_region SET longitude = 115.469, latitude = 35.246 WHERE region_code = '3717';

-- 河南省
UPDATE sys_region SET longitude = 113.665, latitude = 34.757 WHERE region_code = '4101';
UPDATE sys_region SET longitude = 114.341, latitude = 34.797 WHERE region_code = '4102';
UPDATE sys_region SET longitude = 112.434, latitude = 34.663 WHERE region_code = '4103';
UPDATE sys_region SET longitude = 113.307, latitude = 33.735 WHERE region_code = '4104';
UPDATE sys_region SET longitude = 114.352, latitude = 36.103 WHERE region_code = '4105';
UPDATE sys_region SET longitude = 114.295, latitude = 35.748 WHERE region_code = '4106';
UPDATE sys_region SET longitude = 113.883, latitude = 35.302 WHERE region_code = '4107';
UPDATE sys_region SET longitude = 113.238, latitude = 35.239 WHERE region_code = '4108';
UPDATE sys_region SET longitude = 115.041, latitude = 35.768 WHERE region_code = '4109';
UPDATE sys_region SET longitude = 113.826, latitude = 34.022 WHERE region_code = '4110';
UPDATE sys_region SET longitude = 114.026, latitude = 33.575 WHERE region_code = '4111';
UPDATE sys_region SET longitude = 111.194, latitude = 34.777 WHERE region_code = '4112';
UPDATE sys_region SET longitude = 112.540, latitude = 32.999 WHERE region_code = '4113';
UPDATE sys_region SET longitude = 115.650, latitude = 34.437 WHERE region_code = '4114';
UPDATE sys_region SET longitude = 114.075, latitude = 32.123 WHERE region_code = '4115';
UPDATE sys_region SET longitude = 114.649, latitude = 33.620 WHERE region_code = '4116';
UPDATE sys_region SET longitude = 114.024, latitude = 32.980 WHERE region_code = '4117';
UPDATE sys_region SET longitude = 112.719, latitude = 32.990 WHERE region_code = '4190';

-- 湖北省
UPDATE sys_region SET longitude = 114.298, latitude = 30.584 WHERE region_code = '4201';
UPDATE sys_region SET longitude = 115.077, latitude = 30.220 WHERE region_code = '4202';
UPDATE sys_region SET longitude = 110.787, latitude = 32.646 WHERE region_code = '4203';
UPDATE sys_region SET longitude = 111.290, latitude = 30.702 WHERE region_code = '4205';
UPDATE sys_region SET longitude = 112.144, latitude = 32.042 WHERE region_code = '4206';
UPDATE sys_region SET longitude = 114.890, latitude = 30.396 WHERE region_code = '4207';
UPDATE sys_region SET longitude = 112.204, latitude = 31.035 WHERE region_code = '4208';
UPDATE sys_region SET longitude = 113.926, latitude = 30.926 WHERE region_code = '4209';
UPDATE sys_region SET longitude = 112.238, latitude = 30.326 WHERE region_code = '4210';
UPDATE sys_region SET longitude = 114.879, latitude = 30.447 WHERE region_code = '4211';
UPDATE sys_region SET longitude = 114.328, latitude = 29.832 WHERE region_code = '4212';
UPDATE sys_region SET longitude = 113.373, latitude = 31.717 WHERE region_code = '4213';
UPDATE sys_region SET longitude = 112.410, latitude = 30.371 WHERE region_code = '4290';

-- 湖南省
UPDATE sys_region SET longitude = 112.982, latitude = 28.194 WHERE region_code = '4301';
UPDATE sys_region SET longitude = 113.151, latitude = 27.835 WHERE region_code = '4302';
UPDATE sys_region SET longitude = 112.944, latitude = 27.829 WHERE region_code = '4303';
UPDATE sys_region SET longitude = 112.607, latitude = 26.900 WHERE region_code = '4304';
UPDATE sys_region SET longitude = 111.469, latitude = 27.238 WHERE region_code = '4305';
UPDATE sys_region SET longitude = 113.132, latitude = 29.370 WHERE region_code = '4306';
UPDATE sys_region SET longitude = 111.691, latitude = 29.040 WHERE region_code = '4307';
UPDATE sys_region SET longitude = 110.479, latitude = 29.117 WHERE region_code = '4308';
UPDATE sys_region SET longitude = 112.355, latitude = 28.570 WHERE region_code = '4309';
UPDATE sys_region SET longitude = 113.032, latitude = 25.793 WHERE region_code = '4310';
UPDATE sys_region SET longitude = 111.608, latitude = 26.434 WHERE region_code = '4311';
UPDATE sys_region SET longitude = 109.978, latitude = 27.550 WHERE region_code = '4312';
UPDATE sys_region SET longitude = 112.008, latitude = 27.728 WHERE region_code = '4313';
UPDATE sys_region SET longitude = 109.739, latitude = 27.735 WHERE region_code = '4331';

-- 广东省
UPDATE sys_region SET longitude = 113.280, latitude = 23.125 WHERE region_code = '4401';
UPDATE sys_region SET longitude = 113.591, latitude = 24.801 WHERE region_code = '4402';
UPDATE sys_region SET longitude = 114.085, latitude = 22.547 WHERE region_code = '4403';
UPDATE sys_region SET longitude = 113.553, latitude = 22.224 WHERE region_code = '4404';
UPDATE sys_region SET longitude = 116.708, latitude = 23.371 WHERE region_code = '4405';
UPDATE sys_region SET longitude = 113.122, latitude = 23.028 WHERE region_code = '4406';
UPDATE sys_region SET longitude = 113.094, latitude = 22.590 WHERE region_code = '4407';
UPDATE sys_region SET longitude = 110.359, latitude = 21.270 WHERE region_code = '4408';
UPDATE sys_region SET longitude = 110.919, latitude = 21.659 WHERE region_code = '4409';
UPDATE sys_region SET longitude = 112.472, latitude = 23.051 WHERE region_code = '4412';
UPDATE sys_region SET longitude = 114.412, latitude = 23.079 WHERE region_code = '4413';
UPDATE sys_region SET longitude = 116.117, latitude = 24.299 WHERE region_code = '4414';
UPDATE sys_region SET longitude = 115.364, latitude = 22.774 WHERE region_code = '4415';
UPDATE sys_region SET longitude = 114.697, latitude = 23.746 WHERE region_code = '4416';
UPDATE sys_region SET longitude = 111.975, latitude = 21.859 WHERE region_code = '4417';
UPDATE sys_region SET longitude = 113.051, latitude = 23.685 WHERE region_code = '4418';
UPDATE sys_region SET longitude = 113.746, latitude = 23.046 WHERE region_code = '4419';
UPDATE sys_region SET longitude = 113.382, latitude = 22.521 WHERE region_code = '4420';
UPDATE sys_region SET longitude = 116.632, latitude = 23.661 WHERE region_code = '4451';
UPDATE sys_region SET longitude = 116.355, latitude = 23.543 WHERE region_code = '4452';
UPDATE sys_region SET longitude = 112.044, latitude = 22.929 WHERE region_code = '4453';

-- 广西
UPDATE sys_region SET longitude = 108.320, latitude = 22.824 WHERE region_code = '4501';
UPDATE sys_region SET longitude = 109.411, latitude = 24.314 WHERE region_code = '4502';
UPDATE sys_region SET longitude = 110.299, latitude = 25.274 WHERE region_code = '4503';
UPDATE sys_region SET longitude = 111.316, latitude = 23.472 WHERE region_code = '4504';
UPDATE sys_region SET longitude = 109.119, latitude = 21.473 WHERE region_code = '4505';
UPDATE sys_region SET longitude = 108.345, latitude = 21.614 WHERE region_code = '4506';
UPDATE sys_region SET longitude = 108.624, latitude = 21.967 WHERE region_code = '4507';
UPDATE sys_region SET longitude = 109.602, latitude = 23.093 WHERE region_code = '4508';
UPDATE sys_region SET longitude = 110.154, latitude = 22.633 WHERE region_code = '4509';
UPDATE sys_region SET longitude = 106.616, latitude = 23.897 WHERE region_code = '4510';
UPDATE sys_region SET longitude = 111.552, latitude = 24.414 WHERE region_code = '4511';
UPDATE sys_region SET longitude = 108.062, latitude = 24.695 WHERE region_code = '4512';
UPDATE sys_region SET longitude = 109.229, latitude = 23.733 WHERE region_code = '4513';
UPDATE sys_region SET longitude = 107.353, latitude = 22.404 WHERE region_code = '4514';

-- 海南省
UPDATE sys_region SET longitude = 110.353, latitude = 20.017 WHERE region_code = '4601';
UPDATE sys_region SET longitude = 109.508, latitude = 18.247 WHERE region_code = '4602';
UPDATE sys_region SET longitude = 109.577, latitude = 19.517 WHERE region_code = '4604';

-- 四川省
UPDATE sys_region SET longitude = 104.065, latitude = 30.659 WHERE region_code = '5101';
UPDATE sys_region SET longitude = 104.773, latitude = 29.352 WHERE region_code = '5103';
UPDATE sys_region SET longitude = 101.716, latitude = 26.580 WHERE region_code = '5104';
UPDATE sys_region SET longitude = 105.443, latitude = 28.889 WHERE region_code = '5105';
UPDATE sys_region SET longitude = 104.398, latitude = 31.127 WHERE region_code = '5106';
UPDATE sys_region SET longitude = 104.741, latitude = 31.464 WHERE region_code = '5107';
UPDATE sys_region SET longitude = 105.829, latitude = 32.433 WHERE region_code = '5108';
UPDATE sys_region SET longitude = 105.571, latitude = 30.533 WHERE region_code = '5109';
UPDATE sys_region SET longitude = 105.066, latitude = 29.580 WHERE region_code = '5110';
UPDATE sys_region SET longitude = 103.761, latitude = 29.582 WHERE region_code = '5111';
UPDATE sys_region SET longitude = 106.082, latitude = 30.795 WHERE region_code = '5113';
UPDATE sys_region SET longitude = 103.831, latitude = 30.048 WHERE region_code = '5114';
UPDATE sys_region SET longitude = 104.630, latitude = 28.760 WHERE region_code = '5115';
UPDATE sys_region SET longitude = 106.633, latitude = 30.456 WHERE region_code = '5116';
UPDATE sys_region SET longitude = 107.502, latitude = 31.209 WHERE region_code = '5117';
UPDATE sys_region SET longitude = 103.001, latitude = 29.987 WHERE region_code = '5118';
UPDATE sys_region SET longitude = 106.753, latitude = 31.858 WHERE region_code = '5119';
UPDATE sys_region SET longitude = 104.641, latitude = 30.122 WHERE region_code = '5120';
UPDATE sys_region SET longitude = 103.766, latitude = 31.481 WHERE region_code = '5132';
UPDATE sys_region SET longitude = 101.716, latitude = 30.050 WHERE region_code = '5133';
UPDATE sys_region SET longitude = 102.264, latitude = 27.886 WHERE region_code = '5134';

-- 贵州省
UPDATE sys_region SET longitude = 106.713, latitude = 26.578 WHERE region_code = '5201';
UPDATE sys_region SET longitude = 104.846, latitude = 26.584 WHERE region_code = '5202';
UPDATE sys_region SET longitude = 106.937, latitude = 27.706 WHERE region_code = '5203';
UPDATE sys_region SET longitude = 105.932, latitude = 26.245 WHERE region_code = '5204';
UPDATE sys_region SET longitude = 105.285, latitude = 27.301 WHERE region_code = '5205';
UPDATE sys_region SET longitude = 109.191, latitude = 27.718 WHERE region_code = '5206';
UPDATE sys_region SET longitude = 107.987, latitude = 26.583 WHERE region_code = '5223';
UPDATE sys_region SET longitude = 106.655, latitude = 26.254 WHERE region_code = '5226';
UPDATE sys_region SET longitude = 104.897, latitude = 25.390 WHERE region_code = '5227';

-- 云南省
UPDATE sys_region SET longitude = 102.712, latitude = 25.040 WHERE region_code = '5301';
UPDATE sys_region SET longitude = 103.797, latitude = 25.501 WHERE region_code = '5303';
UPDATE sys_region SET longitude = 102.543, latitude = 24.352 WHERE region_code = '5304';
UPDATE sys_region SET longitude = 99.167, latitude = 25.111 WHERE region_code = '5305';
UPDATE sys_region SET longitude = 103.717, latitude = 27.336 WHERE region_code = '5306';
UPDATE sys_region SET longitude = 100.233, latitude = 26.872 WHERE region_code = '5307';
UPDATE sys_region SET longitude = 100.972, latitude = 25.047 WHERE region_code = '5308';
UPDATE sys_region SET longitude = 100.086, latitude = 23.886 WHERE region_code = '5309';
UPDATE sys_region SET longitude = 103.375, latitude = 24.510 WHERE region_code = '5325';
UPDATE sys_region SET longitude = 104.208, latitude = 23.369 WHERE region_code = '5326';
UPDATE sys_region SET longitude = 100.797, latitude = 22.001 WHERE region_code = '5328';
UPDATE sys_region SET longitude = 100.139, latitude = 25.606 WHERE region_code = '5329';
UPDATE sys_region SET longitude = 98.578, latitude = 24.436 WHERE region_code = '5331';
UPDATE sys_region SET longitude = 99.706, latitude = 27.826 WHERE region_code = '5334';

-- 西藏
UPDATE sys_region SET longitude = 91.132, latitude = 29.660 WHERE region_code = '5401';
UPDATE sys_region SET longitude = 97.178, latitude = 31.137 WHERE region_code = '5402';
UPDATE sys_region SET longitude = 88.880, latitude = 29.267 WHERE region_code = '5403';
UPDATE sys_region SET longitude = 91.766, latitude = 31.484 WHERE region_code = '5424';
UPDATE sys_region SET longitude = 78.080, latitude = 32.500 WHERE region_code = '5425';
UPDATE sys_region SET longitude = 80.105, latitude = 32.166 WHERE region_code = '5426';

-- 陕西省
UPDATE sys_region SET longitude = 108.948, latitude = 34.263 WHERE region_code = '6101';
UPDATE sys_region SET longitude = 108.979, latitude = 34.916 WHERE region_code = '6102';
UPDATE sys_region SET longitude = 107.144, latitude = 34.369 WHERE region_code = '6103';
UPDATE sys_region SET longitude = 108.705, latitude = 34.329 WHERE region_code = '6104';
UPDATE sys_region SET longitude = 109.502, latitude = 34.499 WHERE region_code = '6105';
UPDATE sys_region SET longitude = 109.490, latitude = 36.596 WHERE region_code = '6106';
UPDATE sys_region SET longitude = 107.028, latitude = 33.077 WHERE region_code = '6107';
UPDATE sys_region SET longitude = 109.741, latitude = 38.290 WHERE region_code = '6108';
UPDATE sys_region SET longitude = 109.029, latitude = 32.685 WHERE region_code = '6109';
UPDATE sys_region SET longitude = 109.940, latitude = 33.868 WHERE region_code = '6110';

-- 甘肃省
UPDATE sys_region SET longitude = 103.823, latitude = 36.058 WHERE region_code = '6201';
UPDATE sys_region SET longitude = 98.877, latitude = 39.832 WHERE region_code = '6202';
UPDATE sys_region SET longitude = 102.187, latitude = 38.514 WHERE region_code = '6203';
UPDATE sys_region SET longitude = 104.173, latitude = 36.544 WHERE region_code = '6204';
UPDATE sys_region SET longitude = 105.724, latitude = 34.578 WHERE region_code = '6205';
UPDATE sys_region SET longitude = 102.634, latitude = 37.929 WHERE region_code = '6206';
UPDATE sys_region SET longitude = 100.455, latitude = 38.932 WHERE region_code = '6207';
UPDATE sys_region SET longitude = 106.684, latitude = 35.543 WHERE region_code = '6208';
UPDATE sys_region SET longitude = 98.510, latitude = 39.744 WHERE region_code = '6209';
UPDATE sys_region SET longitude = 107.638, latitude = 35.734 WHERE region_code = '6210';
UPDATE sys_region SET longitude = 104.626, latitude = 35.579 WHERE region_code = '6211';
UPDATE sys_region SET longitude = 104.929, latitude = 33.388 WHERE region_code = '6212';
UPDATE sys_region SET longitude = 102.911, latitude = 34.986 WHERE region_code = '6230';
UPDATE sys_region SET longitude = 103.211, latitude = 35.601 WHERE region_code = '6229';

-- 青海省
UPDATE sys_region SET longitude = 101.778, latitude = 36.623 WHERE region_code = '6301';
UPDATE sys_region SET longitude = 102.103, latitude = 36.502 WHERE region_code = '6302';
UPDATE sys_region SET longitude = 100.619, latitude = 36.284 WHERE region_code = '6322';
UPDATE sys_region SET longitude = 102.019, latitude = 36.954 WHERE region_code = '6323';
UPDATE sys_region SET longitude = 98.100, latitude = 36.374 WHERE region_code = '6325';
UPDATE sys_region SET longitude = 97.370, latitude = 33.004 WHERE region_code = '6326';
UPDATE sys_region SET longitude = 96.557, latitude = 35.210 WHERE region_code = '6327';
UPDATE sys_region SET longitude = 97.013, latitude = 35.584 WHERE region_code = '6328';

-- 宁夏
UPDATE sys_region SET longitude = 106.278, latitude = 38.466 WHERE region_code = '6401';
UPDATE sys_region SET longitude = 106.376, latitude = 38.984 WHERE region_code = '6402';
UPDATE sys_region SET longitude = 106.199, latitude = 37.986 WHERE region_code = '6403';
UPDATE sys_region SET longitude = 106.285, latitude = 36.004 WHERE region_code = '6404';
UPDATE sys_region SET longitude = 105.189, latitude = 37.514 WHERE region_code = '6405';

-- 新疆
UPDATE sys_region SET longitude = 87.617, latitude = 43.792 WHERE region_code = '6501';
UPDATE sys_region SET longitude = 84.873, latitude = 45.595 WHERE region_code = '6502';
UPDATE sys_region SET longitude = 89.184, latitude = 42.947 WHERE region_code = '6504';
UPDATE sys_region SET longitude = 93.513, latitude = 42.833 WHERE region_code = '6505';
UPDATE sys_region SET longitude = 87.304, latitude = 44.014 WHERE region_code = '6523';
UPDATE sys_region SET longitude = 86.150, latitude = 41.768 WHERE region_code = '6528';
UPDATE sys_region SET longitude = 80.265, latitude = 41.170 WHERE region_code = '6529';
UPDATE sys_region SET longitude = 76.172, latitude = 39.713 WHERE region_code = '6530';
UPDATE sys_region SET longitude = 75.989, latitude = 39.467 WHERE region_code = '6531';
UPDATE sys_region SET longitude = 79.926, latitude = 37.110 WHERE region_code = '6532';
UPDATE sys_region SET longitude = 81.317, latitude = 43.916 WHERE region_code = '6540';
UPDATE sys_region SET longitude = 91.781, latitude = 43.793 WHERE region_code = '6543';
UPDATE sys_region SET longitude = 87.565, latitude = 44.303 WHERE region_code = '6590';

-- 省级行政区（level=1）经纬度，用于省会坐标
UPDATE sys_region SET longitude = 114.502, latitude = 38.045 WHERE region_code = '13';
UPDATE sys_region SET longitude = 112.549, latitude = 37.857 WHERE region_code = '14';
UPDATE sys_region SET longitude = 111.671, latitude = 40.818 WHERE region_code = '15';
UPDATE sys_region SET longitude = 123.429, latitude = 41.796 WHERE region_code = '21';
UPDATE sys_region SET longitude = 125.324, latitude = 43.886 WHERE region_code = '22';
UPDATE sys_region SET longitude = 126.642, latitude = 45.757 WHERE region_code = '23';
UPDATE sys_region SET longitude = 118.767, latitude = 32.041 WHERE region_code = '32';
UPDATE sys_region SET longitude = 120.153, latitude = 30.287 WHERE region_code = '33';
UPDATE sys_region SET longitude = 117.283, latitude = 31.861 WHERE region_code = '34';
UPDATE sys_region SET longitude = 119.306, latitude = 26.075 WHERE region_code = '35';
UPDATE sys_region SET longitude = 115.892, latitude = 28.676 WHERE region_code = '36';
UPDATE sys_region SET longitude = 117.000, latitude = 36.675 WHERE region_code = '37';
UPDATE sys_region SET longitude = 113.665, latitude = 34.757 WHERE region_code = '41';
UPDATE sys_region SET longitude = 114.298, latitude = 30.584 WHERE region_code = '42';
UPDATE sys_region SET longitude = 112.982, latitude = 28.194 WHERE region_code = '43';
UPDATE sys_region SET longitude = 113.280, latitude = 23.125 WHERE region_code = '44';
UPDATE sys_region SET longitude = 108.320, latitude = 22.824 WHERE region_code = '45';
UPDATE sys_region SET longitude = 110.353, latitude = 20.017 WHERE region_code = '46';
UPDATE sys_region SET longitude = 104.065, latitude = 30.659 WHERE region_code = '51';
UPDATE sys_region SET longitude = 106.713, latitude = 26.578 WHERE region_code = '52';
UPDATE sys_region SET longitude = 102.712, latitude = 25.040 WHERE region_code = '53';
UPDATE sys_region SET longitude = 91.132, latitude = 29.660 WHERE region_code = '54';
UPDATE sys_region SET longitude = 108.948, latitude = 34.263 WHERE region_code = '61';
UPDATE sys_region SET longitude = 103.823, latitude = 36.058 WHERE region_code = '62';
UPDATE sys_region SET longitude = 101.778, latitude = 36.623 WHERE region_code = '63';
UPDATE sys_region SET longitude = 106.278, latitude = 38.466 WHERE region_code = '64';
UPDATE sys_region SET longitude = 87.617, latitude = 43.792 WHERE region_code = '65';

-- 补充：自治州/地区/特殊行政区
UPDATE sys_region SET longitude = 109.488, latitude = 30.272 WHERE region_code = '4228';
UPDATE sys_region SET longitude = 112.338, latitude = 16.831 WHERE region_code = '4603';
UPDATE sys_region SET longitude = 110.349, latitude = 20.022 WHERE region_code = '4690';
UPDATE sys_region SET longitude = 101.546, latitude = 25.041 WHERE region_code = '5323';
UPDATE sys_region SET longitude = 98.854, latitude = 25.850 WHERE region_code = '5333';
UPDATE sys_region SET longitude = 94.362, latitude = 29.654 WHERE region_code = '5404';
UPDATE sys_region SET longitude = 91.773, latitude = 29.237 WHERE region_code = '5405';
UPDATE sys_region SET longitude = 92.051, latitude = 31.476 WHERE region_code = '5406';
UPDATE sys_region SET longitude = 82.074, latitude = 44.903 WHERE region_code = '6527';
UPDATE sys_region SET longitude = 82.985, latitude = 46.746 WHERE region_code = '6542';
