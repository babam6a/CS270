import math

def cal_z_angle(d, x, w) :
    fovx = 62.2
    return float((fovx / w) * x)
    # return float(math.degrees(math.atan(x/d)))

# cos(z_angle) = d/x -> d / cos(z_angle) = x
def cal_trajectory(d, z_angle, h_init, g, y, h, max_v) :
    fovy = 48.8
    x = d / math.cos(math.radians(z_angle))
    cal_h = d * math.tan(natg.radians(fovy / h) * y))
    
    for a in range(1, 900) : # 0.1 ~ 89.9
        rad_a = math.radians(a / 10)
        try :
            v = math.sqrt((g * x * x) / (((h_init-cal_h) + x * math.tan(rad_a)) * 2 * math.cos(rad_a) * math.cos(rad_a)))
        except :
            continue
        if v < max_v :
            return (a / 10, v)
    return (0, 0)

