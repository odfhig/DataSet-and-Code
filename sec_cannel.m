function A = sec_cannel(A, clr, low, high)
    [m, n] = size(A);
    c = 34000;
    fs = 48000;
    deltD = c / fs;
    low_range = ceil(low * 2 / deltD);
    high_range = ceil(high * 2 / deltD);

    for i = 1 : n
        
        for cicnum = 1 : 100
            [a, ~] = xcorr(A(:, i), clr);
            a = a(ceil(length(a)/ 2) : end);
            
            [mx, loc] = findpeaks(a);
            [~, mxloc] = max(mx);
            loc = loc(mxloc);
            if (loc < low_range || loc > high_range) && loc + 480 < m
                minp = 1e9;
                bk = 100;
                for k = 0.01 : 0.01 : 2
                    tmp = A(loc : loc + 479, i) - k * clr;
                    sm = sum(tmp.^2);
                    if sm < minp
                        minp = sm;
                        bk = k;
                    end
                end
                A(loc : loc + 479, i) = A(loc : loc + 479, i) - bk * clr;
            else
                break;
            end
        end
    end
end