export interface OrderSummary {
  total:          number;
  pending:        number;
  delivering:     number;
  delivered:      number;
  cancelled:      number;
  createdToday:   number;
  deliveredToday: number;
}

export interface DashboardResponse {
  orders:        OrderSummary;
  totalProducts: number;
  totalUsers:    number;
}
